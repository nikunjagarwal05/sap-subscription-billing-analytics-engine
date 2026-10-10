@echo off
title Build SAP Subscription Billing Application JAR
echo ==================================================================
echo   Building Application JAR (Excluding Tests) with Maven
echo ==================================================================
echo.

:: Resolve Maven Command
set "MVN_CMD=mvn"
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.2\plugins\maven\lib\maven3\bin\mvn.cmd" (
        set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.2\plugins\maven\lib\maven3\bin\mvn.cmd"
    ) else (
        echo [ERROR] Maven not found.
        pause
        exit /b 1
    )
)

echo [1/2] Running Maven Clean and Package...
call "%MVN_CMD%" clean package -DskipTests

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Maven build failed!
    pause
    exit /b 1
)

echo.
echo ==================================================================
echo   BUILD SUCCESS! JARs Created in 'target/' folder:
echo   1. Standard JAR: target\sap-bill-crawler-1.0.0.jar
echo   2. Runnable Fat JAR: target\sap-bill-crawler-1.0.0-jar-with-dependencies.jar
echo ==================================================================
echo.
echo To run the standalone Fat JAR directly with Java:
echo   java -jar target\sap-bill-crawler-1.0.0-jar-with-dependencies.jar
echo.
pause
