#!/usr/bin/env bash

# NOT WORKS
#java -classpath jsoniter-0.9.15.jar com.jsoniter.static_codegen.StaticCodeGen 1

#WORKS
cd src/main/java
java -classpath /Users/volyx/.m2/repository/com/jsoniter/jsoniter/0.9.12/jsoniter-0.9.12.jar:/Users/volyx/Projects/ratpack-java-demo/target/classes com.jsoniter.StaticCodeGenerator io.github.volyx.ratpack.json.DemoCodegenConfig