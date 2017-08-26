#!/usr/bin/env bash

./mvnw.cmd clean package

docker build --rm  -t ratpack .
