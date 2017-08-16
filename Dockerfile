FROM maven:3-jdk-8

# based on Rocksdb install.md:

# Upgrade your gcc to version at least 4.7 to get C++11 support.
RUN apt-get update -y \
    && apt-get install -y build-essential checkinstall \
    && apt-get install -y libgflags-dev \
    && apt-get install -y libsnappy-dev \
    && apt-get install -y zlib1g-dev \
    && apt-get install -y libbz2-dev \
    && cd /tmp && git clone https://github.com/facebook/rocksdb.git && cd rocksdb && git checkout v5.7.2 && make clean && make install

RUN mkdir /data

COPY build/libs/ratpack-java-demo-1.0-SNAPSHOT-all.jar /

#VOLUME /data:/data

EXPOSE 5050:80
ENTRYPOINT ["java", "-Dprofile=prod", "-jar", "ratpack-java-demo-1.0-SNAPSHOT-all.jar"]
