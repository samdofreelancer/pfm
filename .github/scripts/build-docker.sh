#!/bin/bash
# Build all Docker images using CI-optimized Dockerfiles
# These package pre-built artifacts (JAR, dist) instead of re-building from source
set -euo pipefail

docker compose -f docker-compose.yml -f docker-compose.ci.yml build
docker compose --profile e2e build