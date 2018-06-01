default: start

build:
	docker-compose -f deployment/docker-compose-simulation-scenario1.yml down
	mvn clean package -U -DskipTests -Dmaven.javadoc.skip=true

dockerize: build
	docker build -t squirrel .

start: dockerize
	docker-compose -f deployment/docker-compose-simulation-scenario1.yml up

restart:
	docker-compose -f deployment/docker-compose-simulation-scenario1.yml down
	docker-compose -f deployment/docker-compose-simulation-scenario1.yml up
clean:
	rm -rf data/worker* && rm -rf deployment/scenarios/1/worker*
