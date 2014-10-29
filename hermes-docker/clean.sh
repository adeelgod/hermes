#!/bin/sh

. ../hlu-docker/docker.sh

if [ -f $DOCKER ];
then
    #docker rm hlu_$1
    #docker rmi hlu_$1:$2

    IMAGE="$($DOCKER images --no-trunc -a -q hlu_$1)"

    if [ -z "$IMAGE" ];
    then
        echo "[INFO] No images found: hlu_$1!"
    else
        echo "[INFO] $IMAGE"

        CONTAINERS="$($DOCKER ps --no-trunc -a -q "hlu_$1*")"

        if [ -z "$CONTAINERS" ];
        then
            echo "[INFO] No containers found: hlu_$1!"
        else
            echo "[INFO] $CONTAINERS"

            $DOCKER rm $($DOCKER ps --no-trunc -a -q "hlu_$1*")
        fi

        $DOCKER rmi $($DOCKER images --no-trunc -a -q hlu_$1)
    fi

    ssh-keygen -f "$HOME/.ssh/known_hosts" -R 172.17.0.2
    ssh-keygen -f "$HOME/.ssh/known_hosts" -R 172.17.0.3
    ssh-keygen -f "$HOME/.ssh/known_hosts" -R 172.17.0.4
    ssh-keygen -f "$HOME/.ssh/known_hosts" -R 172.17.0.5
    ssh-keygen -f "$HOME/.ssh/known_hosts" -R 172.17.0.6
    ssh-keygen -f "$HOME/.ssh/known_hosts" -R 172.17.0.7
    ssh-keygen -f "$HOME/.ssh/known_hosts" -R 172.17.0.8
    ssh-keygen -f "$HOME/.ssh/known_hosts" -R 172.17.0.9
else
    echo "[WARN] Docker not installed. Skipping cleanup."
fi
