#!/bin/bash

docker rm -f yoyo_web
docker rmi -f yoyo_web_image
base_path=$(cd `dirname $0`; pwd)
uploads_path="${base_path}/uploads"
mkdir ${uploads_path}
chmod u+x ${base_path}/unzip.sh
${base_path}/unzip.sh
docker build -t 'yoyo_web_image' .
docker run -itd --name yoyo_web -h yoyo_web -v ${uploads_path}:/storage/web_code/uploads -p 8082:82 -p 8083:83 -p 8081:8081 -e TZ="Asia/Shanghai" yoyo_web_image
