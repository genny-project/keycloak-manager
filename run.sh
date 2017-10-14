#!/bin/bash

ENV_FILE=genny.env
./create_genny_env.sh ${ENV_FILE} $1
ENV_FILE=$ENV_FILE docker-compose   up -d
ENV_FILE=$ENV_FILE docker-compose logs -f keycloak-manager 
