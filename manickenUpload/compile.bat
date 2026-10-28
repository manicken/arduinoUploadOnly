
@echo off
setlocal EnableExtensions

set "SCRIPT_DIR=%~dp0"
set "BIN_DIR=%~dp0bin"

set "arduinoInstallDir=G:\arduino-1.8.13"
set "arduinoSketchbookDir=G:\ArduinoSketchbook"
set "className=manickenUpload"

rem Validate project directories
if not exist "%SCRIPT_DIR%src\" (
    echo ERROR: Source directory not found.
    goto error
)

if not exist "%SCRIPT_DIR%tool\" (
    echo ERROR: Tool directory not found.
    goto error
)

if not exist "%arduinoInstallDir%\lib\pde.jar" (
    echo ERROR: Arduino installation not found.
    goto error
)

rem Create build directory
if not exist "%BIN_DIR%\" mkdir "%BIN_DIR%"
if errorlevel 1 goto error

rem Clean only the contents of the known build directory
if exist "%BIN_DIR%\" (
    del /F /Q "%BIN_DIR%\*" 2>nul
    for /D %%D in ("%BIN_DIR%\*") do (
        if exist "%%D\" rmdir /S /Q "%%D"
    )
)

rem Compile
javac -cp "%arduinoInstallDir%\lib\pde.jar;%arduinoInstallDir%\lib\arduino-core.jar;%arduinoInstallDir%\lib\jtouchbar-1.0.0.jar" -d "%BIN_DIR%" "%SCRIPT_DIR%src\*.java"
if errorlevel 1 goto compileError

rem Create JAR
pushd "%BIN_DIR%"
if errorlevel 1 goto error

jar cvf "%className%.jar" *
if errorlevel 1 (
    popd
    goto error
)
popd

rem Copy JAR
copy /Y "%BIN_DIR%\%className%.jar" "%SCRIPT_DIR%tool\%className%.jar"
if errorlevel 1 goto error

rem Install files
if not exist "%arduinoSketchbookDir%\tools\%className%\tool\" mkdir "%arduinoSketchbookDir%\tools\%className%\tool"
if errorlevel 1 goto error

if not exist "%arduinoSketchbookDir%\tools\%className%\src\" mkdir "%arduinoSketchbookDir%\tools\%className%\src"
if errorlevel 1 goto error

copy /Y "%SCRIPT_DIR%tool\*" "%arduinoSketchbookDir%\tools\%className%\tool\"
if errorlevel 1 goto error

copy /Y "%SCRIPT_DIR%src\*" "%arduinoSketchbookDir%\tools\%className%\src\"
if errorlevel 1 goto error

echo ***************
echo *** Success ***
echo ***************
pause
exit /b 0

:compileError
echo ERROR: Compilation failed.
goto error

:error
echo.
echo Installation failed.
pause
exit /b 1
