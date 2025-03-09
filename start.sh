#!/bin/bash
set -x

# this directory should be manually created before ./start.sh is run 
mkdir -p /Users/alinstefanescu/workspaces/jenkins_config
docker compose --profile mongo --profile hello-service up -d 
