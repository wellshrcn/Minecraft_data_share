@echo off
call "%~dp0env.bat"
echo Starting Forge client (dev)...
call gradlew.bat --no-daemon runClient %*
