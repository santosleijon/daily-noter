#!/bin/sh

cd /c/git/daily-noter || exit
podman build -t santosleijon/daily-noter-backend:latest
