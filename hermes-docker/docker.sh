#!/bin/sh
WIKBOOK_FILE="docker_image_$1_dockerfile.wiki"

export PROJECT_VERSION="1.0.0-SNAPSHOT"

export DOCKER="/usr/bin/docker.io"

if [ -f $DOCKER ];
then
    echo ""
else
    echo "[WARN] Setting default Docker CLI."
    export DOCKER="/usr/bin/docker"
fi
