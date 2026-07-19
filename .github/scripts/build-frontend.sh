#!/bin/bash
# Build frontend with npm
set -euo pipefail

cd frontend
npm ci
npm run build