# Deployment instructions

Before using these instructions, you need to build squirrel docker image from repository root:
```
$ make dockerize
```

To deploy squirrel do:
```
make
```

If squirrel was deployed previously make sure to clean up the docker containers:
```
docker-compose down
```
