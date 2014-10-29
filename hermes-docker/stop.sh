#!/bin/sh

. ../hlu-docker/docker.sh

if [ -f $DOCKER ];
then
    $DOCKER stop hlu_$1
fi
