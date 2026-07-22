@echo off
cls
echo ==========================================
echo    BUILDING REPITE CONMIGO
echo ==========================================
echo.
echo Cleaning old APK files...
del /f /q app\build\outputs\apk\debug\*.apk 2>nul
echo.
echo Starting Build Process... Please wait.
call gradlew.bat assembleDebug
echo.
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Build completed successfully!
    if not exist NEW_APK_FOLDER mkdir NEW_APK_FOLDER
    for %%i in (app\build\outputs\apk\debug\*.apk) do (
        echo [FILE] New file: %%~nxi is ready!
        copy /y "%%i" "NEW_APK_FOLDER\%%~nxi" >nul
    )
    explorer app\build\outputs\apk\debug
) else (
    echo.
    echo [ERROR] BUILD FAILED! Please check the error messages above.
)
pause
