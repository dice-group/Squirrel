default: build

build:
	mvn clean package shade:shade -U -DskipTests -Dmaven.javadoc.skip=true
	
dockerize:
	docker build -t squirrel .