#!/bin/bash
# Wait for services to be healthy
set -euo pipefail

wait_for_url() {
  local url="$1"
  local timeout="$2"
  local service_name="$3"
  local interval="${4:-5}"

  echo "Waiting for $service_name to be ready..."
  timeout "$timeout" bash -c "until curl -sf \"$url\" > /dev/null 2>&1; do sleep \"$interval\"; done" || {
    echo "ERROR: $service_name failed to start within ${timeout}s"
    exit 1
  }
  echo "$service_name is ready!"
}

wait_for_url "http://localhost:8080/" 120 "Backend"
wait_for_url "http://localhost:3000/" 60 "Frontend"