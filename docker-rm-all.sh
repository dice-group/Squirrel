docker ps -a | grep -v ^CONTAINER | cut -c1-5 | xargs docker rm
