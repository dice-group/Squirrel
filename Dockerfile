FROM java
#FROM ubuntu:trusty

#RUN apt-get update && apt-get -y install python-software-properties && apt-get -y install software-properties-common && add-apt-repository ppa:webupd8team/java -y && apt-get update
#RUN apt-get install -y supervisor
# Accept the Java licenses
# RUN echo oracle-java6-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
# RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
#RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
#RUN apt-get install -y oracle-java8-installer && apt-get install -y oracle-java8-set-default

#COPY ./src /data/squirrel/src
#COPY ./repository /data/squirrel/repository
#COPY ./pom.xml /data/squirrel/pom.xml
COPY ./target/squirrel.jar /data/squirrel/squirrel.jar
WORKDIR /data/squirrel
#RUN mvn clean compile assembly:single

#COPY ./data /data/squirrel/data
#COPY ./scripts /data/squirrel/scripts

#ENV PATH $PATH:/data/squirrel/scripts

# Frontier tcp port
#EXPOSE 60000
# Sink tcp port
#EXPOSE 60001

VOLUME ["/var/squirrel/data"]
