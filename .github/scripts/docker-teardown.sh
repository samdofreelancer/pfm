#!/bin/bash
# Tear down Docker Compose services
set -euo pipefail

docker compose -f docker-compose.yml -f docker-compose.ci.yml down --remove-orphans
echo "Docker services stopped and cleaned up."