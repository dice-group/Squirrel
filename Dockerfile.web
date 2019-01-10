FROM openjdk:8u151-jdk

RUN apt-get update && apt-get install -y netcat

COPY ./squirrel.web/target/squirrel.web.jar /data/squirrel/squirrel.web.jar
COPY ./squirrel.web/target/squirrel.web.jar.original /data/squirrel/squirrel.web.jar.original
COPY ./squirrel.web/WEB-INF /data/squirrel/WEB-INF 
WORKDIR /data/squirrel

#ADD entrypoint.sh /entrypoint.sh
#RUN chmod +x /entrypoint.sh

VOLUME ["/var/squirrel/data"]

CMD java -cp squirrel.web.jar:. com.squirrel.Application

