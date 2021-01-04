@echo off

set arduinoInstallDir=G:\arduino-1.8.13
#set arduinoSketchbookDir=%HOMEDRIVE%%HOMEPATH%\Documents\Arduino
set arduinoSketchbookDir=G:\ArduinoSketchbook
set className=manickenUpload

cd bin
del/F/Q/S *
cd ..

mkdir bin
javac -cp "%arduinoInstallDir%\lib\pde.jar;%arduinoInstallDir%\lib\arduino-core.jar;%arduinoInstallDir%\lib\jtouchbar-1.0.0.jar" -d bin src\*.java
if errorlevel 1 goto compileError
cd bin
jar cvf %className%.jar *
cd ..
copy .\bin\%className%.jar .\tool\%className%.jar

mkdir %arduinoSketchbookDir%\tools\%className%\tool\
mkdir %arduinoSketchbookDir%\tools\%className%\src\

copy %~dp0tool\* %arduinoSketchbookDir%\tools\%className%\tool\*
copy %~dp0src\* %arduinoSketchbookDir%\tools\%className%\src\*

echo cleanup for github upload
cd bin
del/F/Q/S *
cd ..
echo ***************
echo *** Success ***
echo ***************
pause
exit

:compileError
echo Compile error
pause