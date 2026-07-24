@echo off
call "%~dp0env.bat"
call gradlew.bat --no-daemon build --no-configuration-cache
if errorlevel 1 exit /b 1

set "VER_NAME=1.21.1-Forge_52.1.16"
set "MODS_DIR=%MC_ROOT%\.minecraft\versions\%VER_NAME%\mods"
if not exist "%MODS_DIR%" mkdir "%MODS_DIR%"

set "JAR=%~dp0build\libs\data_share-1.0.0.jar"
if not exist "%JAR%" (
  echo No data_share jar found: %JAR%
  exit /b 1
)

del /Q "%MODS_DIR%\data_share-*.jar" 2>nul
copy /Y "%JAR%" "%MODS_DIR%\" >nul
echo Installed: %JAR%
echo   -^> %MODS_DIR%