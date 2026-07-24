@echo off
call "%~dp0env.bat"
echo Building mod jar...
call gradlew.bat --no-daemon build %*
if errorlevel 1 exit /b 1
echo.
echo Output jars:
dir /b "%~dp0build\libs\*.jar" 2>nul
echo.
echo Tip: copy the jar (not -sources) into:
echo   %~dp0..\.minecraft\mods\
