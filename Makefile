default: build

build:
	mvn clean package -U -DskipTests -Dmaven.javadoc.skip=true

dockerize: build
	docker build -t squirrel .

start: dockerize
	docker-compose -f docker-compose-simulation-scenario1.yml up

clean:
	rm -rf data/worker*
