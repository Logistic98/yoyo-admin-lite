#!/bin/bash

/docker-entrypoint.sh nginx -g 'daemon off;' &
java -jar /storage/web_manage-0.0.1.jar --spring.profiles.active=prod