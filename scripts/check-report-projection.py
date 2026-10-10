"""Offline host test using already provisioned Kotlin/Groovy jars; no downloads."""
from pathlib import Path
import argparse,os,subprocess,hashlib
p=argparse.ArgumentParser()
p.add_argument('--tools',type=Path,required=True,help='Provisioned .tools/alfred directory')
p.add_argument('--report',type=Path,required=True,help='Retained ABBA product JSON (not copied into source)')
a=p.parse_args(); repo=Path(__file__).resolve().parents[1]; out=repo/'build/report-android/host-checks';out.mkdir(parents=True,exist_ok=True)
cache=a.tools/'gradle-user-home/caches/modules-2/files-2.1'
def jar(group,name,version):return next((cache/group/name/version).rglob(name+'-'+version+'.jar'))
stdlib=jar('org.jetbrains.kotlin','kotlin-stdlib','2.2.21')
compiler=[jar('org.jetbrains.kotlin','kotlin-compiler-embeddable','2.2.21'),stdlib,
    jar('org.jetbrains.kotlin','kotlin-script-runtime','2.2.21'),
    jar('org.jetbrains.kotlin','kotlin-reflect','1.6.10'),
    jar('org.jetbrains.kotlinx','kotlinx-coroutines-core-jvm','1.8.0'),
    jar('org.jetbrains','annotations','13.0')]
groovy=list((a.tools/'gradle-8.13/lib').glob('groovy-3*.jar'))+list((a.tools/'gradle-8.13/lib').glob('groovy-json-*.jar'))
cp=os.pathsep.join(str(x.resolve()) for x in [stdlib,*groovy,compiler[-1]])
java=a.tools/'jdk-17.0.16+8/bin/java.exe'
sources=[repo/'feature-forensics/src/main/java/dev/alfred/forensics'/n for n in ['ReportModel.kt','ReportRules.kt']]+[repo/'scripts/ReportProjectionChecks.kt']
before=hashlib.sha256(a.report.read_bytes()).hexdigest()
command=[str(java.resolve()),'-cp',os.pathsep.join(str(x.resolve()) for x in compiler),'org.jetbrains.kotlin.cli.jvm.K2JVMCompiler','-no-stdlib','-no-reflect','-jvm-target','17','-classpath',cp,'-d',str(out/'checks.jar'),*map(str,sources)]
subprocess.run(command,cwd=repo,check=True)
subprocess.run([str(java.resolve()),'-cp',str(out/'checks.jar')+os.pathsep+cp,'dev.alfred.forensics.ReportProjectionChecksKt',str(a.report.resolve())],cwd=repo,check=True)
assert before==hashlib.sha256(a.report.read_bytes()).hexdigest()
print('Original report SHA-256 unchanged:',before)
