FROM openjdk:8u151-jdk

RUN apt-get update && apt-get install -y netcat

COPY ./target/squirrel.jar /data/squirrel/squirrel.jar
WORKDIR /data/squirrel

ADD entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

VOLUME ["/var/squirrel/data"]

ENTRYPOINT ["/bin/bash", "/entrypoint.sh"]
