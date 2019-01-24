FROM openjdk:8u151-jdk

RUN apt-get update && apt-get install -y netcat

COPY ./squirrel.worker/target/squirrel.worker.jar /data/squirrel/squirrel.jar
COPY ./spring-config /data/squirrel/spring-config
WORKDIR /data/squirrel

#ADD entrypoint.sh /entrypoint.sh
#RUN chmod +x /entrypoint.sh

VOLUME ["/var/squirrel/data"]

CMD java -cp squirrel.jar:. org.aksw.simba.squirrel.components.WorkerComponentStarter
