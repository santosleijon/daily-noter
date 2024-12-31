#!/bin/bash

podman run -e POSTGRES_PASSWORD=secret -p 5432:5432 --name daily-noter-db --detach daily-noter-db
