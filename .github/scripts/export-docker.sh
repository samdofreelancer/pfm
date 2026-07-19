#!/bin/bash
# Save Docker images and upload as artifact
set -euo pipefail

OUTPUT_DIR="${1:-/tmp/docker-images}"

mkdir -p "$OUTPUT_DIR"
docker save pfm_backend pfm_frontend pfm_e2e -o "$OUTPUT_DIR/docker-images.tar"
echo "Docker images saved to $OUTPUT_DIR/docker-images.tar"