@echo off

cd %~dp0
start "First step: compiling..." /D %~dp0 /NORMAL /WAIT cmd /C mvn clean package
cd target

if EXIST "original-*.jar" (
    del /F SquirrelWebService*.jar
)

copy /B /Y *.jar start.jar
move start.jar ../
cd ..

docker build -t squirrel/webimage .

del start.jar

cd ..
