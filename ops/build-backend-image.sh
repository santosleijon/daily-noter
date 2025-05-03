#!/bin/sh

IMAGE=santosleijon/daily-noter-backend:latest

cd /c/git/daily-noter || exit
mvn clean install
podman build -t $IMAGE .
podman push $IMAGE