default: build

build:
	mvn clean package shade:shade -U -DskipTests -Dmaven.javadoc.skip=true
	
dockerize: build
	docker build -t squirrel .

clean:
	rm -rf data/worker*
