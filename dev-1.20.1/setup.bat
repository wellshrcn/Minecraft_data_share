@echo off
call "%~dp0env.bat"
echo.
echo === Forge MDK setup (1.20.1-47.4.22) ===
echo GRADLE_USER_HOME=%GRADLE_USER_HOME%
echo.
echo [1/3] Download dependencies + prepare workspace...
call gradlew.bat --no-daemon genIntellijRuns
if errorlevel 1 goto :fail
echo.
echo [2/3] Also generate Eclipse run configs...
call gradlew.bat --no-daemon genEclipseRuns
if errorlevel 1 goto :fail
echo.
echo [3/3] Compile example mod...
call gradlew.bat --no-daemon build
if errorlevel 1 goto :fail
echo.
echo === Setup OK ===
echo Next:
echo   - Cursor/VS Code: open folder  mod\mc\dev
echo   - IntelliJ: Import Gradle project from build.gradle, then use runClient
echo   - Quick test:   runClient.bat
echo   - Build jar:    build.bat   -^>  build\libs\*.jar
echo   - Copy jar to:  ..\.minecraft\mods\
exit /b 0
:fail
echo.
echo === Setup FAILED ===
exit /b 1
