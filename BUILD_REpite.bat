@echo off
echo Cleaning old files...
del /f /q app\build\outputs\apk\debug\REPITE_V12.apk 2>nul
echo Building Repite Conmigo v11.8.0... Please wait.
call gradlew.bat assembleDebug
echo.
if %ERRORLEVEL% EQU 0 (
    echo BUILD SUCCESSFUL!
    echo New file: REPITE_V12.apk (v11.8.0) is ready!
    explorer app\build\outputs\apk\debug
) else (
    echo.
    echo BUILD FAILED! Please take a screenshot of the errors above.
)
pause
