#!/bin/bash

# ============================================
# PFM - Personal Finance Manager Startup Script
# ============================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}"
echo "  ╔══════════════════════════════════════╗"
echo "  ║   PFM - Personal Finance Manager     ║"
echo "  ║     Starting Application...          ║"
echo "  ╚══════════════════════════════════════╝"
echo -e "${NC}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Parse args
START_BACKEND=true
START_FRONTEND=true
START_E2E=false
E2E_AFTER_START=false
KEEP_E2E=false
USE_DOCKER=false
USE_DEVTOOLS=false

while [[ "$#" -gt 0 ]]; do
    case $1 in
        --backend-only) START_FRONTEND=false; START_E2E=false; E2E_AFTER_START=false; KEEP_E2E=false ;;
        --frontend-only) START_BACKEND=false; START_E2E=false; E2E_AFTER_START=false; KEEP_E2E=false ;;
        --e2e-only) START_BACKEND=false; START_FRONTEND=false; E2E_AFTER_START=true ;;
        --e2e) E2E_AFTER_START=true ;;
        --keep-e2e) KEEP_E2E=true ;;
        --docker) USE_DOCKER=true ;;
        --dev) USE_DEVTOOLS=true ;;
        --help)
            echo "Usage: $0 [--backend-only | --frontend-only | --e2e-only | --e2e | --keep-e2e | --docker | --dev | --help]"
            echo ""
            echo "Options:"
            echo "  --backend-only  Start only the backend service"
            echo "  --frontend-only Start only the frontend service"
            echo "  --e2e-only      Run only the e2e tests (starts backend/frontend in Docker)"
            echo "  --e2e           Run e2e tests after starting services (requires --docker)"
            echo "  --keep-e2e      Keep e2e container after tests for debugging"
            echo "  --docker        Run services in Docker containers"
            echo "  --dev           Enable hot reload for local development"
            echo "  --help          Show this help message"
            exit 0
            ;;
        *) echo "Unknown option: $1"; exit 1 ;;
    esac
    shift
done

# If --e2e or --e2e-only is set, --docker is implied
if [ "$E2E_AFTER_START" = true ] || [ "$START_E2E" = true ]; then
    USE_DOCKER=true
fi

# Check prereqs (only for non-Docker mode)
check_command() {
    if ! command -v "$1" &>/dev/null; then
        echo -e "${RED}✗ $1 is not installed.${NC}"
        exit 1
    fi
}

# Only check java/mvn/node/npm when not using Docker
if [ "$USE_DOCKER" = false ]; then
    echo -e "${YELLOW}Checking prerequisites...${NC}"
    check_command java
    check_command mvn
    check_command node
    check_command npm
fi

echo -e "${YELLOW}Checking Docker...${NC}"
check_command docker
echo -e "${YELLOW}→ Checking Docker Compose...${NC}"
if command -v docker-compose &>/dev/null; then
    DOCKER_COMPOSE="docker-compose"
elif docker compose version &>/dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    echo -e "${RED}✗ No Docker Compose found (neither 'docker-compose' nor 'docker compose').${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Using: $DOCKER_COMPOSE${NC}"
echo -e "${GREEN}✓ All prerequisites found.${NC}"

# ===========================
# DATABASE (Docker)
# ===========================
echo ""
echo -e "${CYAN}══════════════════════════════════════${NC}"
echo -e "${CYAN}  Starting Database (Docker)...      ${NC}"
echo -e "${CYAN}══════════════════════════════════════${NC}"
echo ""

# Check if docker-compose exists
if [ -f "docker-compose.yml" ]; then
    # Check if containers are already running
    POSTGRES_RUNNING=$(docker ps --format '{{.Names}}' 2>/dev/null | grep -c "pfm-postgres" || true)
    REDIS_RUNNING=$(docker ps --format '{{.Names}}' 2>/dev/null | grep -c "pfm-redis" || true)

    if [ "$POSTGRES_RUNNING" = "0" ] || [ "$REDIS_RUNNING" = "0" ]; then
        echo -e "${YELLOW}→ Starting Docker containers (PostgreSQL + Redis)...${NC}"
        $DOCKER_COMPOSE up -d
        echo -e "${GREEN}✓ Docker containers started.${NC}"
    else
        echo -e "${GREEN}✓ Docker containers already running.${NC}"
    fi

    # Wait for PostgreSQL to be healthy
    echo -e "${YELLOW}→ Waiting for PostgreSQL to be ready...${NC}"
    RETRIES=30
    until docker exec pfm-postgres pg_isready -U pfm_user -d pfm >/dev/null 2>&1 || [ $RETRIES -eq 0 ]; do
        echo -n "."
        sleep 2
        RETRIES=$((RETRIES-1))
    done
    echo ""

    if [ $RETRIES -eq 0 ]; then
        echo -e "${RED}✗ PostgreSQL failed to start in time.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ PostgreSQL is ready!${NC}"

    # Wait for Redis
    echo -e "${YELLOW}→ Waiting for Redis to be ready...${NC}"
    RETRIES=15
    until docker exec pfm-redis redis-cli ping >/dev/null 2>&1 || [ $RETRIES -eq 0 ]; do
        echo -n "."
        sleep 1
        RETRIES=$((RETRIES-1))
    done
    echo ""

    if [ $RETRIES -eq 0 ]; then
        echo -e "${RED}✗ Redis failed to start in time.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Redis is ready!${NC}"
else
    echo -e "${RED}✗ docker-compose.yml not found.${NC}"
    exit 1
fi

# ===========================
# BACKEND
# ===========================
if [ "$START_BACKEND" = true ]; then
    if [ "$USE_DOCKER" = true ]; then
        echo ""
        echo -e "${CYAN}══════════════════════════════════════${NC}"
        echo -e "${CYAN}  Starting Backend (Docker)...       ${NC}"
        echo -e "${CYAN}══════════════════════════════════════${NC}"
        echo ""

        echo -e "${YELLOW}→ Building and starting backend container...${NC}"
        $DOCKER_COMPOSE up -d --build backend
        echo -e "${GREEN}✓ Backend container started.${NC}"
    else
        echo ""
        echo -e "${CYAN}══════════════════════════════════════${NC}"
        if [ "$USE_DEVTOOLS" = true ]; then
            echo -e "${CYAN}  Building & Starting Backend...     ${NC}"
            echo -e "${CYAN}  (Hot Reload Enabled)              ${NC}"
        else
            echo -e "${CYAN}  Building & Starting Backend...     ${NC}"
        fi
        echo -e "${CYAN}══════════════════════════════════════${NC}"
        echo ""

        cd backend

        echo -e "${YELLOW}→ Building all modules (install)...${NC}"
        mvn install -DskipTests -q
        echo -e "${GREEN}✓ Backend modules built & installed.${NC}"

        if [ "$USE_DEVTOOLS" = true ]; then
            echo -e "${GREEN}→ Starting Spring Boot with Hot Reload on http://localhost:8080${NC}"
            echo -e "${YELLOW}  (DevTools enabled - auto-restart on code changes)${NC}"
        else
            echo -e "${GREEN}→ Starting Spring Boot on http://localhost:8080${NC}"
        fi
        mvn spring-boot:run -pl pfm-bootstrap &
        BACKEND_PID=$!

        cd "$SCRIPT_DIR"
    fi
fi

# ===========================
# FRONTEND
# ===========================
if [ "$START_FRONTEND" = true ]; then
    if [ "$USE_DOCKER" = true ]; then
        echo ""
        echo -e "${CYAN}══════════════════════════════════════${NC}"
        echo -e "${CYAN}  Starting Frontend (Docker)...       ${NC}"
        echo -e "${CYAN}══════════════════════════════════════${NC}"
        echo ""

        echo -e "${YELLOW}→ Building and starting frontend container...${NC}"
        $DOCKER_COMPOSE up -d --build frontend
        echo -e "${GREEN}✓ Frontend container started.${NC}"
    else
        echo ""
        echo -e "${CYAN}══════════════════════════════════════${NC}"
        if [ "$USE_DEVTOOLS" = true ]; then
            echo -e "${CYAN}  Starting Frontend (Vite + React)...${NC}"
            echo -e "${CYAN}  (HMR Enabled)                      ${NC}"
        else
            echo -e "${CYAN}  Starting Frontend (Vite + React)...${NC}"
        fi
        echo -e "${CYAN}══════════════════════════════════════${NC}"
        echo ""

        cd frontend

        if [ ! -d "node_modules" ]; then
            echo -e "${YELLOW}→ Installing frontend dependencies...${NC}"
            npm install
            echo -e "${GREEN}✓ Dependencies installed.${NC}"
        fi

        if [ "$USE_DEVTOOLS" = true ]; then
            echo -e "${GREEN}→ Starting Vite with HMR on http://localhost:3000${NC}"
            echo -e "${YELLOW}  (Hot Module Replacement enabled)${NC}"
        else
            echo -e "${GREEN}→ Starting Vite on http://localhost:3000${NC}"
        fi
        npm run dev &
        FRONTEND_PID=$!

        cd "$SCRIPT_DIR"
    fi
fi

# ===========================
# E2E
# ===========================
if [ "$E2E_AFTER_START" = true ]; then
    echo ""
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo -e "${CYAN}  Running E2E Tests (Docker)...       ${NC}"
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo ""

    echo -e "${YELLOW}→ Building and starting e2e container...${NC}"
    # Don't use set -e for e2e tests, capture exit code instead
    set +e
    $DOCKER_COMPOSE up --build e2e
    E2E_EXIT_CODE=$?
    set -e
    
    if [ $E2E_EXIT_CODE -eq 0 ]; then
        echo -e "${GREEN}✓ E2E tests passed!${NC}"
    else
        echo -e "${RED}✗ E2E tests failed!${NC}"
    fi
    
    # Cleanup e2e container after tests (unless --keep-e2e is set)
    if [ "$KEEP_E2E" = true ]; then
        echo -e "${YELLOW}→ Keeping e2e container for debugging. Run 'docker-compose rm -f e2e' to clean up.${NC}"
    else
        echo -e "${YELLOW}→ Cleaning up e2e container...${NC}"
        $DOCKER_COMPOSE rm -f e2e
        echo -e "${GREEN}✓ E2E container cleaned up.${NC}"
    fi
fi

# ===========================
# SUMMARY
# ===========================
echo ""
echo -e "${GREEN}╔══════════════════════════════════════╗${NC}"
echo -e "${GREEN}║     Application Started! 🚀         ║${NC}"
echo -e "${GREEN}╠══════════════════════════════════════╣${NC}"
echo -e "${GREEN}║  Database: PostgreSQL :5432          ║${NC}"
echo -e "${GREEN}║  Cache:    Redis      :6379          ║${NC}"
if [ "$START_BACKEND" = true ]; then
    echo -e "${GREEN}║  Backend:  http://localhost:8080     ║${NC}"
fi
if [ "$START_FRONTEND" = true ]; then
    echo -e "${GREEN}║  Frontend: http://localhost:3000     ║${NC}"
fi
if [ "$E2E_AFTER_START" = true ]; then
    echo -e "${GREEN}║  E2E:      Tests completed           ║${NC}"
fi
echo -e "${GREEN}╚══════════════════════════════════════╝${NC}"
echo ""
if [ "$USE_DEVTOOLS" = true ]; then
    echo -e "${YELLOW}💡 Hot Reload is ENABLED:${NC}"
    echo -e "${YELLOW}   • Backend: Spring Boot DevTools (auto-restart on code changes)${NC}"
    echo -e "${YELLOW}   • Frontend: Vite HMR (instant browser updates)${NC}"
    echo ""
fi
if [ "$E2E_AFTER_START" = false ]; then
    echo -e "${YELLOW}Press Ctrl+C to stop all services.${NC}"
fi

cleanup() {
    echo ""
    echo -e "${YELLOW}Shutting down...${NC}"
    if [ "$USE_DOCKER" = true ]; then
        echo -e "${YELLOW}Stopping Docker containers...${NC}"
        $DOCKER_COMPOSE down
        echo -e "${GREEN}✓ Docker containers stopped.${NC}"
    else
        if [ -n "$BACKEND_PID" ]; then
            kill "$BACKEND_PID" 2>/dev/null
            echo -e "${GREEN}✓ Backend stopped.${NC}"
        fi
        if [ -n "$FRONTEND_PID" ]; then
            kill "$FRONTEND_PID" 2>/dev/null
            echo -e "${GREEN}✓ Frontend stopped.${NC}"
        fi
    fi
    echo -e "${CYAN}Goodbye!${NC}"
    exit 0
}

trap cleanup SIGINT SIGTERM
wait