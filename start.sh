#!/bin/bash

# ============================================
# PFM - Personal Finance Manager Startup Script
# ============================================

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}"
echo "  ╔══════════════════════════════════════╗"
echo "  ║   PFM - Personal Finance Manager     ║"
echo "  ║     Starting Application...          ║"
echo "  ╚══════════════════════════════════════╝"
echo -e "${NC}"

# Determine project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

# Check prerequisites
check_command() {
    if ! command -v "$1" &>/dev/null; then
        echo -e "${RED}✗ $1 is not installed. Please install it first.${NC}"
        exit 1
    fi
}

echo -e "${YELLOW}Checking prerequisites...${NC}"
check_command java
check_command mvn
check_command node
check_command npm
echo -e "${GREEN}✓ All prerequisites found.${NC}"

# Parse arguments
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

# Create data directory for PostgreSQL if using Docker
if ! docker ps --format '{{.Names}}' | grep -q "pfm-postgres"; then
    echo -e "${YELLOW}PostgreSQL container not detected. Make sure your database is running.${NC}"
    echo -e "${YELLOW}You can start it with: docker run -d --name pfm-postgres -e POSTGRES_DB=pfm -e POSTGRES_USER=pfm_user -e POSTGRES_PASSWORD=pfm_password -p 5432:5432 postgres:15${NC}"
fi

# Start Backend
if [ "$START_BACKEND" = true ]; then
    echo ""
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo -e "${CYAN}  Starting Backend (Spring Boot)...  ${NC}"
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo ""

    cd backend || exit 1

    # Clean and compile
    echo -e "${YELLOW}→ Compiling backend...${NC}"
    if mvn compile -q; then
        echo -e "${GREEN}✓ Backend compiled successfully.${NC}"
    else
        echo -e "${RED}✗ Backend compilation failed.${NC}"
        exit 1
    fi

    # Run Spring Boot
    echo -e "${GREEN}→ Starting Spring Boot on http://localhost:8080${NC}"
    mvn spring-boot:run -pl pfm-bootstrap &
    BACKEND_PID=$!

    cd "$SCRIPT_DIR" || exit 1
fi

# Start Frontend
if [ "$START_FRONTEND" = true ]; then
    echo ""
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo -e "${CYAN}  Starting Frontend (Vite + React)...${NC}"
    echo -e "${CYAN}══════════════════════════════════════${NC}"
    echo ""

    cd frontend || exit 1

    # Install dependencies if needed
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}→ Installing frontend dependencies...${NC}"
        if npm install; then
            echo -e "${GREEN}✓ Dependencies installed.${NC}"
        else
            echo -e "${RED}✗ npm install failed.${NC}"
            exit 1
        fi
    fi

    # Start Vite dev server
    echo -e "${GREEN}→ Starting Vite on http://localhost:3000${NC}"
    npm run dev &
    FRONTEND_PID=$!

    cd "$SCRIPT_DIR" || exit 1
fi

# Print summary
echo ""
echo -e "${GREEN}╔══════════════════════════════════════╗${NC}"
echo -e "${GREEN}║     Application Started! 🚀         ║${NC}"
echo -e "${GREEN}╠══════════════════════════════════════╣${NC}"
if [ "$START_BACKEND" = true ]; then
    echo -e "${GREEN}║  Backend:  http://localhost:8080     ║${NC}"
    echo -e "${GREEN}║  API:      http://localhost:8080/api/v1 ║${NC}"
fi
if [ "$START_FRONTEND" = true ]; then
    echo -e "${GREEN}║  Frontend: http://localhost:3000     ║${NC}"
fi
echo -e "${GREEN}╚══════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop all services.${NC}"

# Trap Ctrl+C to kill both processes
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
    echo -e "${CYAN}Goodbye!${NC}"
    exit 0
}

trap cleanup SIGINT SIGTERM

# Wait for both processes
wait