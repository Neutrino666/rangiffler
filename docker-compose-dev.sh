#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=dev
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ARCH=$(uname -m)

docker compose down
docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rangiffler')
fast=false

usage() {
  cat <<EOF
Скрипт поднятия Rangiffler в docker compose.

Examples:
${0##*/} [-f]
${0##*/}

Параметры:
  -f         fast - режим переиспользования images
  -h         help
EOF
}

while getopts ":fh" opt; do
  case $opt in
  f) fast=true;;
  h) usage ; exit 0;;
  \?) echo "Неизвестная опция -$OPTARG" >&2; usage ; exit 0 ;;
  esac
done

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ $fast = false ]; then
  if [ ! -z "$docker_images"  ]; then
      echo "### Remove images: $docker_images ###"
        docker rmi $docker_images
  fi
fi

echo '### Java version ###'
java --version
bash ./gradlew clean

if [ "$1" = "push" ]; then
  echo "### Build & push images ###"
  bash ./gradlew jib -Duser.timezone=UTC
  docker compose push frontend.rangiffler.dc
else
  echo "### Build images ###"
  bash ./gradlew jibDockerBuild -Duser.timezone=UTC
fi

docker compose up -d
docker ps -a