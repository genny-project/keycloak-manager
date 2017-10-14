#!/bin/bash
if [ -z "${1}" ]; then
   version="latest"
else
   version="${1}"
fi

docker push gennyproject/wildfly-qwanda-service:"${version}"
docker tag gennyproject/wildfly-qwanda-service:"${version}" gennyproject/wildfly-qwanda-service:latest
docker push gennyproject/wildfly-qwanda-service:latest
