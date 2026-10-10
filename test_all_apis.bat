@echo off
title SAP Real-Time 5-API Integration & AI Test Suite
echo ==================================================================
echo   SAP Subscription Billing: Real-Time API Test Suite
echo   Testing 5 Integrated APIs + Auto-POJO + Database + AI Engine
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

call "%MVN_CMD%" compile exec:java -Dexec.mainClass="projectsap.sap.test.RealtimeApiTester"

echo.
pause
