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

while [[ "$#" -gt 0 ]]; do
    case $1 in
        --backend-only) START_FRONTEND=false ;;
        --frontend-only) START_BACKEND=false ;;
        --help)
            echo "Usage: $0 [--backend-only | --frontend-only | --help]"
            exit 0
            ;;
        *) echo "Unknown option: $1"; exit 1 ;;
    esac
    shift
done

# Check prereqs
check_command() {
    if ! command -v "$1" &>/dev/null; then
        echo -e "${RED}✗ $1 is not installed.${NC}"
        exit 1
    fi
}
echo -e "${YELLOW}Checking prerequisites...${NC}"
check_command java
check_command mvn
check_command node
check_command npm
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
    echo ""
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo -e "${CYAN}  Building & Starting Backend...     ${NC}"
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo ""

    cd backend

    echo -e "${YELLOW}→ Building all modules (install)...${NC}"
    mvn install -DskipTests -q
    echo -e "${GREEN}✓ Backend modules built & installed.${NC}"

    echo -e "${GREEN}→ Starting Spring Boot on http://localhost:8080${NC}"
    mvn spring-boot:run -pl pfm-bootstrap &
    BACKEND_PID=$!

    cd "$SCRIPT_DIR"
fi

# ===========================
# FRONTEND
# ===========================
if [ "$START_FRONTEND" = true ]; then
    echo ""
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo -e "${CYAN}  Starting Frontend (Vite + React)...${NC}"
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo ""

    cd frontend

    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}→ Installing frontend dependencies...${NC}"
        npm install
        echo -e "${GREEN}✓ Dependencies installed.${NC}"
    fi

    echo -e "${GREEN}→ Starting Vite on http://localhost:3000${NC}"
    npm run dev &
    FRONTEND_PID=$!

    cd "$SCRIPT_DIR"
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
echo -e "${GREEN}╚══════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop all services.${NC}"

cleanup() {
    echo ""
    echo -e "${YELLOW}Shutting down...${NC}"
    if [ -n "$BACKEND_PID" ]; then
        kill "$BACKEND_PID" 2>/dev/null
        echo -e "${GREEN}✓ Backend stopped.${NC}"
    fi
    if [ -n "$FRONTEND_PID" ]; then
        kill "$FRONTEND_PID" 2>/dev/null
        echo -e "${GREEN}✓ Frontend stopped.${NC}"
    fi
    echo -e "${YELLOW}Stopping Docker containers...${NC}"
    $DOCKER_COMPOSE down
    echo -e "${GREEN}✓ Docker containers stopped.${NC}"
    echo -e "${CYAN}Goodbye!${NC}"
    exit 0
}

trap cleanup SIGINT SIGTERM
wait