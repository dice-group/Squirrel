default: build

build:
	docker-compose -f docker-compose.yml down
	mvn clean install -U -DskipTests -Dmaven.javadoc.skip=true

dockerize:
	docker build -f Dockerfile.frontier -t dicegroup/squirrel.frontier .
	docker build -f Dockerfile.worker -t dicegroup/squirrel.worker .
	docker build -f Dockerfile.web -t squirrel.web .
	docker build -f Dockerfile.mockup -t dicegroup/squirrel.mockup .

push-images:
	docker push dicegroup/squirrel.frontier
	docker push dicegroup/squirrel.worker
	docker push dicegroup/squirrel.mockup

tag-orca-images:
	docker tag dicegroup/squirrel.frontier git.project-hobbit.eu:4567/ldcbench/ldcbench-squirrel-adapter/squirrel-frontier
	docker tag dicegroup/squirrel.worker git.project-hobbit.eu:4567/ldcbench/ldcbench-squirrel-adapter/squirrel-worker

push-orca-images:
	docker push git.project-hobbit.eu:4567/ldcbench/ldcbench-squirrel-adapter/squirrel-frontier
	docker push git.project-hobbit.eu:4567/ldcbench/ldcbench-squirrel-adapter/squirrel-worker

start: dockerize
	docker-compose -f docker-compose-sparql.yml up

restart:
	docker-compose -f docker-compose-sparql.yml down
	docker-compose -f docker-compose-sparql.yml up
clean:
	rm -rf data/worker* && rm -rf deployment/scenarios/1/worker* && rm -rf data/sparqlhost/sparqlhost_data/databases
