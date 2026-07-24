@echo off
REM All Gradle caches / wrappers stay under mod\mc.
set "MC_ROOT=%~dp0.."
set "GRADLE_USER_HOME=%MC_ROOT%\.gradle"
if not exist "%GRADLE_USER_HOME%" mkdir "%GRADLE_USER_HOME%"

REM Gradle runs on installed Java 17; ForgeGradle toolchains download/use Java 21 for compilation.
if exist "C:\Program Files\Microsoft\jdk-17.0.13.11-hotspot\bin\java.exe" (
  set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.13.11-hotspot"
  set "PATH=%JAVA_HOME%\bin;%PATH%"
)

cd /d "%~dp0"