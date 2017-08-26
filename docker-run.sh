#!/usr/bin/env bash

docker run -v /Users/volyx/Projects/hlcupdocs/data/FULL:/tmp -v /Users/volyx/Projects/hlcupdocs/data/FULL/data/options.txt:/tmp/data/options.txt -p 8080:80 ratpack

