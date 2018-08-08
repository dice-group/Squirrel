# Dockerfile for building the Docker image for the Squirrel-Webservice
FROM java:latest
LABEL version="0.5.0"

COPY ./ /etc/store

WORKDIR /etc/store
ENTRYPOINT  java -jar start.jar

#EXPOSE 8080
HEALTHCHECK --interval=60s --timeout=3s CMD curl -f http://localhost/ || exit 1
