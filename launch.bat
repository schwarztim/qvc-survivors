@echo off
REM QVC Survivors launcher for Windows
REM Requires Java 17+ installed
for %%f in ("%~dp0QVCSurvivors-*.jar") do set JAR=%%f
if "%JAR%"=="" (
    echo Error: QVCSurvivors JAR not found
    pause
    exit /b 1
)
java -cp "%JAR%" com.qvc.survivors.Launcher %*
