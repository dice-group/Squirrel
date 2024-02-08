default: build

write-version:
	mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -Doutput=version.txt

build:
	docker-compose -f docker-compose.yml down
	mvn clean install -U -DskipTests -Dmaven.javadoc.skip=true

dockerize: write-version
	docker build -f Dockerfile.frontier -t dicegroup/squirrel.frontier:"$$(cat version.txt)" .
	docker build -f Dockerfile.worker -t dicegroup/squirrel.worker:"$$(cat version.txt)" .
#	docker build -f Dockerfile.web -t squirrel.web:"$$(cat version.txt)" .
	docker build -f Dockerfile.mockup -t dicegroup/squirrel.mockup:"$$(cat version.txt)" .

push-images: write-version
	docker push dicegroup/squirrel.frontier:"$$(cat version.txt)"
	docker push dicegroup/squirrel.worker:"$$(cat version.txt)"
	docker push dicegroup/squirrel.mockup:"$$(cat version.txt)"

tag-orca-images: write-version
	docker tag dicegroup/squirrel.frontier:"$$(cat version.txt)" git.project-hobbit.eu:4567/ldcbench/ldcbench-squirrel-adapter/squirrel-frontier:"$$(cat version.txt)"
	docker tag dicegroup/squirrel.worker:"$$(cat version.txt)" git.project-hobbit.eu:4567/ldcbench/ldcbench-squirrel-adapter/squirrel-worker:"$$(cat version.txt)"

push-orca-images: write-version
	docker push git.project-hobbit.eu:4567/ldcbench/ldcbench-squirrel-adapter/squirrel-frontier:"$$(cat version.txt)"
	docker push git.project-hobbit.eu:4567/ldcbench/ldcbench-squirrel-adapter/squirrel-worker:"$$(cat version.txt)"

tag-latest: write-version
	docker tag dicegroup/squirrel.frontier:"$$(cat version.txt)" dicegroup/squirrel.frontier:latest
	docker tag dicegroup/squirrel.worker:"$$(cat version.txt)" dicegroup/squirrel.worker:latest
	docker tag dicegroup/squirrel.mockup:"$$(cat version.txt)" dicegroup/squirrel.mockup:latest

push-latest-images:
	docker push dicegroup/squirrel.frontier:latest
	docker push dicegroup/squirrel.worker:latest
	docker push dicegroup/squirrel.mockup:latest

start: dockerize
	docker-compose -f docker-compose-sparql.yml up

restart:
	docker-compose -f docker-compose-sparql.yml down
	docker-compose -f docker-compose-sparql.yml up
clean:
	rm -rf data/worker* && rm -rf deployment/scenarios/1/worker* && rm -rf data/sparqlhost/sparqlhost_data/databases
