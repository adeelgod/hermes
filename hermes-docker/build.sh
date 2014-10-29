#!/bin/sh
WIKBOOK_FILE="docker_image_$1_dockerfile.wiki"

. ../hlu-docker/docker.sh

if [ -f $DOCKER ];
then
    $DOCKER build -t hlu_$1:$2 .
else
    echo "[WARN] Docker not installed. Skipping build."
fi
