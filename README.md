# Squirrel
Squirrel searches and collects Linked Data

# Running with docker
```
  $ make build dockerize
  $ docker-compose build
  $ docker-compose up
```

## Dependencies

The [Squirrel-Webservice](https://github.com/phhei/Squirrel-Webservice) and the [SquirrelWebObject](https://github.com/phhei/SquirrelWebObject) are included in this project, now. This leads to the fact, that this project is a multi module maven project now. For that, there are 2 pom.xml's in the roor layer:
- `pom.xml`: this is the module bundle pom xml. If you execute ``mvn clean package``, this file will be called. As a consequence from this, all submodules including the _squirrel_ will be complied an packed
- `squirrel-pom.xml`: the pom for the _squirrel_

If you want to run the squirrel with the **Webserice**, take care that you have already the current Webservice image (Docker). If not, execute
1. ``mvn clean package``
1. (``SquirrelWebObject\install.bat``)
1. ``SquirrelWebService\buildImage.bat``

### deprecated
~~You need 2 additional repositories to (at least stay in the current version) the web component:~~

- [Squirrel-Webservice](https://github.com/phhei/Squirrel-Webservice)
  - ``mvn clean package``
  - please execute the ``buildImage.bat`` in the top directory layer
    - to use the local respository from the project, execute ``mvn install:install-file -DgroupId=org.aksw.simba.squirrel -DartifactId=SquirrelWebObject -Dpackaging=jar -Dversion=<currentVersion> -Dfile=<pathToSquirrelWebObject>\target\SquirrelWebObjectJar-<currentVersion>.jar -DgeneratePom=true -DlocalRepositoryPath=.\repository``
- [SquirrelWebObject](https://github.com/phhei/SquirrelWebObject)
  - ``mvn clean package``
  - please execute the ``install.bat`` in the top directory layer
