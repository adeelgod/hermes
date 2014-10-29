#!/bin/sh

. ../hlu-docker/docker.sh

if [ -f $DOCKER ] && [ $3 = "true" ];
then
    #$DOCKER save hlu_$1 > target/hlu_$1.tar
    #gzip target/hlu_$1.tar
    echo "[INFO] === Skipping export for hlu_$1."
else
    echo "[INFO] === Skipping export for hlu_$1."
fi
