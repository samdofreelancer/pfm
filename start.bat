@echo off
chcp 65001 >nul

echo ╔══════════════════════════════════════╗
echo ║   PFM - Personal Finance Manager     ║
echo ║     Starting Application...          ║
echo ╚══════════════════════════════════════╝
echo.

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

:: Parse arguments
set START_BACKEND=1
set START_FRONTEND=1
set USE_DOCKER=0
set USE_DEVTOOLS=0

if "%1"=="--backend-only" set START_FRONTEND=0
if "%1"=="--frontend-only" set START_BACKEND=0
if "%1"=="--docker" set USE_DOCKER=1
if "%1"=="--dev" set USE_DEVTOOLS=1
if "%1"=="--help" (
    echo Usage: start.bat [--backend-only ^| --frontend-only ^| --docker ^| --dev ^| --help]
    exit /b 0
)

:: ===========================
:: DATABASE (Docker)
:: ===========================
echo ═══════════════════════════════════════
echo   Starting Database (Docker)...
echo ═══════════════════════════════════════
echo.

:: Check if docker-compose containers already running
docker ps --format "{{.Names}}" 2>nul | find "pfm-postgres" >nul
set POSTGRES_RUNNING=%errorlevel%
docker ps --format "{{.Names}}" 2>nul | find "pfm-redis" >nul
set REDIS_RUNNING=%errorlevel%

if %POSTGRES_RUNNING% neq 0 (
    echo → Starting Docker containers (PostgreSQL + Redis)...
    docker-compose up -d
    if errorlevel 1 (
        echo ✗ Failed to start Docker containers. Is Docker running?
        pause
        exit /b 1
    )
    echo ✓ Docker containers started.
) else (
    echo ✓ Docker containers already running.
)

:: Wait for PostgreSQL
echo → Waiting for PostgreSQL to be ready...
set RETRIES=30
:wait_pg
docker exec pfm-postgres pg_isready -U pfm_user -d pfm >nul 2>&1
if errorlevel 1 (
    set /a RETRIES=RETRIES-1
    if %RETRIES% equ 0 (
        echo ✗ PostgreSQL failed to start in time.
        pause
        exit /b 1
    )
    timeout /t 2 /nobreak >nul
    goto wait_pg
)
echo ✓ PostgreSQL is ready!

:: Wait for Redis
echo → Waiting for Redis to be ready...
set RETRIES=15
:wait_redis
docker exec pfm-redis redis-cli ping >nul 2>&1
if errorlevel 1 (
    set /a RETRIES=RETRIES-1
    if %RETRIES% equ 0 (
        echo ✗ Redis failed to start in time.
        pause
        exit /b 1
    )
    timeout /t 1 /nobreak >nul
    goto wait_redis
)
echo ✓ Redis is ready!

:: ===========================
:: BACKEND
:: ===========================
if %START_BACKEND%==1 (
    echo.
    echo ═══════════════════════════════════════
    echo   Building ^& Starting Backend...
    echo ═══════════════════════════════════════
    echo.

    if %USE_DOCKER%==1 (
        echo → Building and starting backend container...
        docker-compose up -d --build backend
        echo ✓ Backend container started.
    ) else (
        cd backend

        echo → Building all modules (install)...
        call mvn install -DskipTests -q
        if errorlevel 1 (
            echo ✗ Backend build failed.
            pause
            exit /b 1
        )
        echo ✓ Backend modules built ^& installed.

        if %USE_DEVTOOLS%==1 (
            echo → Starting Spring Boot with Hot Reload on http://localhost:8080
            echo   (DevTools enabled - auto-restart on code changes)
        ) else (
            echo → Starting Spring Boot on http://localhost:8080
        )
        start "PFM-Backend" cmd /c "mvn spring-boot:run -pl pfm-bootstrap"

        cd /d "%SCRIPT_DIR%"
    )
)

:: ===========================
:: FRONTEND
:: ===========================
if %START_FRONTEND%==1 (
    echo.
    echo ═══════════════════════════════════════
    echo   Starting Frontend...
    echo ═══════════════════════════════════════
    echo.

    if %USE_DOCKER%==1 (
        echo → Building and starting frontend container...
        docker-compose up -d --build frontend
        echo ✓ Frontend container started.
    ) else (
        cd frontend

        if not exist "node_modules" (
            echo → Installing frontend dependencies...
            call npm install
            if errorlevel 1 (
                echo ✗ npm install failed.
                pause
                exit /b 1
            )
            echo ✓ Dependencies installed.
        )

        if %USE_DEVTOOLS%==1 (
            echo → Starting Vite with HMR on http://localhost:3000
            echo   (Hot Module Replacement enabled)
        ) else (
            echo → Starting Vite on http://localhost:3000
        )
        start "PFM-Frontend" cmd /c "npm run dev"

        cd /d "%SCRIPT_DIR%"
    )
)

:: ===========================
:: SUMMARY
:: ===========================
echo.
echo ╔══════════════════════════════════════╗
echo ║     Application Started! 🚀         ║
echo ╠══════════════════════════════════════╣
echo ║  Database: PostgreSQL :5432          ║
echo ║  Cache:    Redis      :6379          ║
if %START_BACKEND%==1 (
    echo ║  Backend:  http://localhost:8080     ║
)
if %START_FRONTEND%==1 (
    echo ║  Frontend: http://localhost:3000     ║
)
echo ╚══════════════════════════════════════╝
echo.
if %USE_DEVTOOLS%==1 (
    echo 💡 Hot Reload is ENABLED:
    echo    • Backend: Spring Boot DevTools (auto-restart on code changes)
    echo    • Frontend: Vite HMR (instant browser updates)
    echo.
)
echo Close the terminal windows to stop all services.
echo To stop Docker containers later, run: docker-compose down
pause