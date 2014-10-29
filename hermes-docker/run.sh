#!/bin/bash
. ../hlu-docker/docker.sh

# Parameters:
# 1. Detached container true/false
# 2. Restart true/false
# 3. Image name
# 4. Volumes
# 5. Links
# 6. Ports
# 7. Environment variables file

DETACHED=$1
RESTART=$2
NAME=$3
VOLUMES=($4)
LINKS=($5)
PORTS=($6)
ENVFILE=($7)

DOCKER_OPTS=""

INSTANCE=$($DOCKER ps | grep $NAME)

echo "==========================="
echo "="
echo "= $NAME"
echo "="
echo "==========================="

if [ -z INSTANCE ];
then
    echo "[INFO] Starting new container: $NAME"
else
    echo "[INFO] Running container found"
    echo "[INFO] '$INSTANCE'"
    if [ $RESTART = "true" ]
    then
        echo "[WARN] Stopping previous container instance: $NAME"
        $DOCKER stop $NAME
    else
        echo "[WARN] Exiting."
        exit 0
    fi
fi

if [ -z $VOLUMES ];
then
    echo "[WARN] No volumes selected."
else
    for i in ${VOLUMES[@]}; do
        echo "[INFO] Selecting volumes from: ${i}."
        DOCKER_OPTS="$DOCKER_OPTS --volumes-from ${i}"
    done
fi

if [ -z $LINKS ];
then
    echo "[WARN] No linked containers."
else
    for i in ${LINKS[@]}; do
        echo "[INFO] Linking to: ${i}."
        DOCKER_OPTS="$DOCKER_OPTS --link ${i}"
    done
fi

if [ -z $PORTS ];
then
    echo "[WARN] No port mappings."
else
    for i in ${PORTS[@]}; do
        echo "[INFO] Port mapping: ${i}."
        DOCKER_OPTS="$DOCKER_OPTS -p ${i}"
    done
fi

if [ -z $ENVFILE ];
then
    echo "[WARN] No environment variables file."
else
    DOCKER_OPTS="$DOCKER_OPTS --env-file $ENVFILE"
fi

if [ -f $DOCKER ];
then
    if [ $DETACHED = "true" ]
    then
        INSTANCE=$($DOCKER ps -a | grep $NAME)
        if [ -z INSTANCE ]
        then
            echo "[INFO] No previous container instance found. Continuing container startup."
        else
            echo "[WARN] Previous container instance found. Deleting stop container: $NAME."
            $DOCKER rm $NAME
        fi
        echo "[INFO] Starting detached container $NAME."
        $DOCKER run --hostname $NAME --name $NAME -d -t $DOCKER_OPTS $NAME:$PROJECT_VERSION
        echo "$DOCKER run --hostname $NAME --name $NAME -d -t $DOCKER_OPTS $NAME:$PROJECT_VERSION"
    else
        echo "[INFO] Execute this to start an attached container for $NAME:"
        echo "$DOCKER run --hostname $NAME --name $NAME --rm -i -t $DOCKER_OPTS $NAME:$PROJECT_VERSION /bin/zsh"
        exit 0
    fi
fi
