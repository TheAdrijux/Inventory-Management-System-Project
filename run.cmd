@echo off
title Inventory Management System
color 0A

echo Starting Inventory Management System...
echo.

REM Check if target folder exists, if not do a full build
if not exist "target" (
    echo First time setup - Building project...
    call mvn clean package
    echo.
)

echo Launching application...
echo.

REM Run with Maven JavaFX plugin
call mvn javafx:run

if errorlevel 1 (
    color 0C
    echo.
    echo Failed to start! Trying alternative method...
    echo.
    timeout /t 2 > nul
    
    REM Try alternative method
    call mvn clean javafx:run
    
    if errorlevel 1 (
        color 0C
        echo.
        echo Application failed to start!
        echo Please make sure MySQL is running and the database is set up.
        echo.
        pause
        exit /b 1
    )
)

echo.
echo Application closed. Press any key to exit.
pause > nul 