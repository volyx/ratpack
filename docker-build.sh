#!/usr/bin/env bash

./gradlew clean shadowJar

docker build --rm  -t ratpack .
