default: start

build:
	docker-compose -f docker-compose-sparql.yml down
	mvn clean package -U -DskipTests -Dmaven.javadoc.skip=true

dockerize: build
	docker build -t squirrel .

start: dockerize
	docker-compose -f docker-compose-sparql.yml up

restart:
	docker-compose -f docker-compose-sparql.yml down
	docker-compose -f docker-compose-sparql.yml up
clean:
	rm -rf data/worker* && rm -rf deployment/scenarios/1/worker* && rm -rf data/sparqlhost/sparqlhost_data/databases
