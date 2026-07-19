#!/bin/bash
# Build backend with Maven
set -euo pipefail

cd backend
mvn -B clean verify "$@"