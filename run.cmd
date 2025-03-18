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

REM Create shortcut on desktop
echo.
echo Creating desktop shortcut...
echo Set oWS = WScript.CreateObject("WScript.Shell") > CreateShortcut.vbs
echo sLinkFile = oWS.ExpandEnvironmentStrings("%USERPROFILE%\Desktop\Inventory System.lnk") >> CreateShortcut.vbs
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> CreateShortcut.vbs
echo oLink.TargetPath = "%CD%\target\InventorySystem.exe" >> CreateShortcut.vbs
echo oLink.WorkingDirectory = "%CD%" >> CreateShortcut.vbs
echo oLink.Description = "Inventory Management System" >> CreateShortcut.vbs
echo oLink.IconLocation = "%CD%\target\InventorySystem.exe,0" >> CreateShortcut.vbs
echo oLink.Save >> CreateShortcut.vbs
cscript //nologo CreateShortcut.vbs
del CreateShortcut.vbs

echo.
echo Desktop shortcut created! You can now run the application from the shortcut.
echo Note: Run this script again if you make code changes to rebuild the application.
echo.
echo Application closed. Press any key to exit.
pause > nul 