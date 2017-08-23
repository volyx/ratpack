FROM openjdk:8u131-jre-alpine

RUN mkdir /data

COPY build/libs/ratpack-java-demo-1.0-SNAPSHOT-all.jar /

EXPOSE 80
ENTRYPOINT ["java", "-Dprofile=prod", "-jar", "ratpack-java-demo-1.0-SNAPSHOT-all.jar"]
