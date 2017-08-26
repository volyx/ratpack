FROM openjdk:8u131-jre-alpine

RUN mkdir /data

COPY build/libs/ratpack-java-demo-1.0-SNAPSHOT-all.jar /

CMD sysctl -w net.core.rmem_max=16777216 \
    sysctl -w net.core.wmem_max=16777216 \
    sysctl -w net.ipv4.tcp_rmem="4096 87380 16777216" \
    sysctl -w net.ipv4.tcp_wmem="4096 16384 16777216" \
    sysctl -w net.core.somaxconn=4096 \
    sysctl -w net.core.netdev_max_backlog=16384 \
    sysctl -w net.ipv4.tcp_max_syn_backlog=8192 \
    sysctl -w net.ipv4.tcp_syncookies=1 \
    sysctl -w net.ipv4.ip_local_port_range="1024 65535" \
    sysctl -w net.ipv4.tcp_tw_recycle=1 \

EXPOSE 80
ENTRYPOINT ["java", "-Xmx4G", "-Xms256m", "-Dprofile=prod", "-jar", "ratpack-java-demo-1.0-SNAPSHOT-all.jar"]
