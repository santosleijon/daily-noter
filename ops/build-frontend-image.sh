#!/bin/sh

IMAGE=santosleijon/daily-noter-frontend:latest

cd /c/git/daily-noter/ui || exit
podman build -t $IMAGE .
podman push $IMAGE