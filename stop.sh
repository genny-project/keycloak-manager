#!/bin/bash
ENV_FILE=genny.env docker-compose   stop $@
ENV_FILE=genny.env docker-compose  rm -f $@

