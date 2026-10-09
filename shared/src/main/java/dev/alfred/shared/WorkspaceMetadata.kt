package dev.alfred.shared

import android.os.Looper
import android.content.Context
import android.net.Uri
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/** Selection-scoped original probe files. Readers pin output; replacement releases owners.
 * At most 32 outputs / 512 MiB are retained, and only one document is parsed by the UI.
 * This cache is not an analysis result and never claims decoded PCM or quality.
 */
internal class WorkspaceMetadata : AutoCloseable {
    private data class Entry(val output: OwnedDirectory, val file: File, val size: Long, val hash: String,
                             val lease: AutoCloseable)
    private val entries = mutableMapOf<String, Entry>()
    private val readerLock = Any()

    @Synchronized fun adopt(item: String, attempt: String, result: Map<String, Any?>, output: OwnedDirectory) {
        if (result["directory"] != attempt) throw InputFailure("invalid_payload")
        val descriptor = result["payload"] as? Map<*, *> ?: throw InputFailure("invalid_payload")
        if (descriptor["kind"] != "metadata" || descriptor["version"] != "1") throw InputFailure("unsupported_version")
        val path = descriptor["path"] as? String ?: throw InputFailure("invalid_payload")
        if (File(path).isAbsolute || path.contains('/') || path.contains('\\') || path in setOf(".", "..")) throw InputFailure("invalid_payload")
        val dir = File(output.file, attempt)
        val file = File(dir, path)
        val size = descriptor["bytes"] as? BigInteger ?: throw InputFailure("invalid_payload")
        if (dir.canonicalFile.parentFile != output.file.canonicalFile || file.canonicalFile.parentFile != dir.canonicalFile ||
            !file.isFile || size != BigInteger.valueOf(file.length()) || file.length() > ResultStore.DOCUMENT_LIMIT) throw InputFailure("invalid_payload")
        val hash = descriptor["sha256"] as? String ?: throw InputFailure("invalid_payload")
        validate(file, file.length(), hash)
        val used = entries.values.sumOf { it.output.file.walkTopDown().filter(File::isFile).sumOf(File::length) }
        val added = output.file.walkTopDown().filter(File::isFile).sumOf(File::length)
        if (entries.size >= 32 || used + added > 512L * 1024 * 1024) throw InputFailure("storage_full")
        val entry = Entry(output, file, file.length(), hash, output.retain())
        entries.put(item, entry)?.lease?.close()
    }

    fun read(item: String): Any? = synchronized(readerLock) {
        check(Looper.myLooper() != Looper.getMainLooper()) { "Metadata reads must run off main" }
        val (entry, pin) = synchronized(this) {
            val entry = entries[item] ?: throw InputFailure("input_missing")
            entry to entry.output.retain()
        }
        pin.use {
            validate(entry.file, entry.size, entry.hash)
            val runtime = Runtime.getRuntime()
            if (runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory() < entry.size * 8 + 16L * 1024 * 1024) throw InputFailure("resource_limit")
            ExactJson.parse(entry.file.readBytes(), ResultStore.DOCUMENT_LIMIT.toInt())
        }
    }

    fun export(item: String, context: Context, destination: Uri) {
        check(Looper.myLooper() != Looper.getMainLooper()) { "Metadata export must run off main" }
        val (entry, pin) = synchronized(this) {
            val entry = entries[item] ?: throw InputFailure("input_missing")
            entry to entry.output.retain()
        }
        pin.use {
            validate(entry.file, entry.size, entry.hash)
            val output = context.contentResolver.openOutputStream(destination, "w") ?: throw InputFailure("io_error")
            output.use { target -> entry.file.inputStream().use { it.copyTo(target, InputStore.BUFFER_BYTES) } }
        }
    }

    @Synchronized override fun close() {
        val previous = entries.values.toList()
        entries.clear()
        previous.forEach { it.lease.close() }
    }

    private fun validate(file: File, size: Long, hash: String) {
        if (!file.isFile || file.length() != size) throw InputFailure("invalid_payload")
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(InputStore.BUFFER_BYTES)
            while (true) { val count = input.read(buffer); if (count < 0) break; digest.update(buffer, 0, count) }
        }
        if (digest.digest().joinToString("") { "%02x".format(it) } != hash) throw InputFailure("invalid_payload")
    }
}
