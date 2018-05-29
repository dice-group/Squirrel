# Squirrel
Squirrel searches and collects Linked Data

## Running with docker

### Using the Makefile...

```
  $ make build dockerize
  $ docker-compose build
  $ docker-compose up
```

### ... or do it manually

1. ``mvn clean package shade:shade -U -DskipTests``
1. If you want to use the Web-Components, have a look to the Depencies in this file
1. ``docker build -t squirrel .``
1. execute a `.yml` file with ``docker-compose -f <file> up``/ ``down``

#### There are currently 2 yml-options

- `docker-compose-simulation-scenario1.yml`: there are 3 HTTP-nodes (simulated in Docker) with 7 URIs in total. Without Web-Service!
- `docker-compose-simulation-scenario1-with-web.yml`: there are 3 HTTP-nodes (simulated in Docker) with 7 URIs in total. With Web-Service!

---
## Dependencies

The [Squirrel-Webservice](https://github.com/phhei/Squirrel-Webservice) and the [SquirrelWebObject](https://github.com/phhei/SquirrelWebObject) are included in this project, now. This leads to the fact, that this project is a multi module maven project now. For that, there are 2 pom.xml's in the root layer:
- `pom.xml`: this is the module bundle pom xml. If you execute ``mvn clean package``, this file will be called. As a consequence from this, all submodules including the _squirrel_ will be complied an packed
- `squirrel-pom.xml`: the pom for the _squirrel_

If you want to run the squirrel with the **Webservice**, take care that you have already the current Webservice image (Docker). If not, execute
1. ``mvn clean package`` _(only necessary if you want to compile each subproject (module) for itself)_
1. (``SquirrelWebObject\install.bat``)
1. ``SquirrelWebService\buildImage.bat``
