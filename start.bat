@echo off
echo Starting Kniffel game...
cd /d "%~dp0"
call mvnw clean javafx:run
pause
