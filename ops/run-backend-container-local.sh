#!/bin/sh

cd /c/git/daily-noter || exit
podman run -p 8080:8080 \
  --name daily-noter-backend \
  --env-file local-podman.env \
  daily-noter-backend
