@echo off
call "%~dp0env.bat"
call gradlew.bat --no-daemon build %*