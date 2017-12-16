default: build dockerize

build:
	mvn clean package -U -DskipTests -Dmaven.javadoc.skip=true

dockerize: build
	docker build -t squirrel .

clean:
	rm -rf data/worker*
