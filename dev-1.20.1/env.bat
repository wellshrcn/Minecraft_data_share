@echo off
REM All Gradle caches / wrappers stay under mod\mc (never leave this tree).
set "MC_ROOT=%~dp0.."
set "GRADLE_USER_HOME=%MC_ROOT%\.gradle"
if not exist "%GRADLE_USER_HOME%" mkdir "%GRADLE_USER_HOME%"

REM Prefer the Microsoft JDK 17 already on this machine (Forge 1.20.1 requires 17).
if exist "C:\Program Files\Microsoft\jdk-17.0.13.11-hotspot\bin\java.exe" (
  set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.13.11-hotspot"
  set "PATH=%JAVA_HOME%\bin;%PATH%"
)

cd /d "%~dp0"
