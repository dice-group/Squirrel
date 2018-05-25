@echo off & setlocal

echo We will install the current SquirrelWebObject to your maven repository now!

cd target

for %%f IN (*.jar) DO (
set file=%%~ff
set filename=%%~nf
)

echo Found file %file% (%filename%)

for /F "tokens=2 delims=-" %%i IN ("%filename%") DO set version=%%i

echo Current Version is %version%. Install it!

cd ..

mvn install:install-file -DgroupId=org.aksw.simba.squirrel -DartifactId=SquirrelWebObject -Dpackaging=jar -Dversion=%version% -Dfile=%file% -DgeneratePom=true