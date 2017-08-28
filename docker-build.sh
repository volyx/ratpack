#!/usr/bin/env bash

./mvnw clean package

docker build --rm  -t ratpack .
