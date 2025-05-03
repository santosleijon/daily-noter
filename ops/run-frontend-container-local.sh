#!/bin/sh

podman run -p 8081:8081 \
  --name daily-noter-frontend \
  -d \
  -e VITE_API_URL=http://localhost:8080/api \
  santosleijon/daily-noter-frontend:latest
