default: build

build:
	mvn clean package -U -DskipTests -Dmaven.javadoc.skip=true
	
dockerize: build
	docker build -t squirrel .

clean:
	rm -rf data/worker* && rm -rf deployment/scenarios/1/worker*
