#!/bin/bash
source ./docker.properties
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ARCH=$(uname -m)

docker compose down
docker_containers=$(docker ps -a --format '{{.Names}}' | grep -E 'allure|selenoid')

if [ ! -z "$docker_containers" ]; then
  echo -e "### \nStop and remove containers: \n$docker_containers\n###"
  docker stop $docker_containers && docker rm $docker_containers
fi

docker pull twilio/selenoid:chrome_stable_145

echo '### Run mode ###'

docker compose -f docker-compose-tests-local-env.yml up -d
docker ps -a