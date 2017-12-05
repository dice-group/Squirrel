# Deployment instructions

SSH into server and clone this repo:
```
ssh akswnc5.aksw.uni-leipzig.de
git clone https://github.com/dice-group/Squirrel && cd Squirrel
```

Before using these instructions, you need to build squirrel docker image from repository root:
```
$ make dockerize
```

To deploy squirrel do:
```
cd deployment && make
```

If squirrel was deployed previously make sure to clean up the docker containers:
```
docker-compose down
```
