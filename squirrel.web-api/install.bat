@echo off & setlocal

echo We will install the current SquirrelWebObject to your maven repository now!

cd %~dp0
cd target

if EXIST "original-*.jar" (
    del /F /P "SquirrelWebObject*.jar"
    if %errorlevel% == 0 ren "original-*.jar" "/////////*.jar"
)

for %%f IN (*.jar) DO (
    set file=%%~ff
    set filename=%%~nf
)

echo Found file %file% (%filename%)

for /F "tokens=2 delims=-" %%i IN ("%filename%") DO set version=%%i

echo Current Version is %version%. Install it!

cd ..

mvn install:install-file -DgroupId=org.aksw.simba.squirrel -DartifactId=SquirrelWebObject -Dpackaging=jar -Dversion=%version% -Dfile="%file%" -DgeneratePom=true -DlocalRepositoryPath=..\repository

cd ..
