#!/bin/bash
#rsync -zrt --progress build/repo/ katalisi@ssh.katalisindonesia.com:/home1/katalisi/public_html2/maven.katalisindonesia.com/repo

set -e
BUILD_ID=${BUILD_ID:-${GITHUB_RUN_NUMBER}}
ARTIFACT=banyuwangi-core
echo Displaying content of build/libs
find build/libs

export version=0.0.$BUILD_ID
echo "$DOCKER_HUB_PASSWORD" | docker login -u ptkis --password-stdin
docker rmi "ptkis/$ARTIFACT:$version" || true
docker build \
  --build-arg "ARTIFACT=$ARTIFACT" \
  --build-arg "VERSION=$version" \
 -t ptkis/$ARTIFACT:latest -t "ptkis/$ARTIFACT:$version" . && \
docker image inspect "ptkis/$ARTIFACT:$version" || exit 1
docker push ptkis/$ARTIFACT:latest && \
  docker push "ptkis/$ARTIFACT:$version"
