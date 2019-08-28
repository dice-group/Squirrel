default: build

build:
	docker-compose -f docker-compose.yml down
	mvn clean install -U -DskipTests -Dmaven.javadoc.skip=true

dockerize:
	docker build -f Dockerfile.frontier -t squirrel.frontier -t dicegroup/squirrel-frontier .
	docker build -f Dockerfile.worker -t squirrel.worker -t dicegroup/squirrel-worker .
	docker build -f Dockerfile.web -t squirrel.web .
	docker build -f Dockerfile.mockup -t squirrel.mockup .

push-images:
	docker push dicegroup/squirrel-frontier
	docker push dicegroup/squirrel-worker

start: dockerize
	docker-compose -f docker-compose-sparql.yml up

restart:
	docker-compose -f docker-compose-sparql.yml down
	docker-compose -f docker-compose-sparql.yml up
clean:
	rm -rf data/worker* && rm -rf deployment/scenarios/1/worker* && rm -rf data/sparqlhost/sparqlhost_data/databases
