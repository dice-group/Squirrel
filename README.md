# Squirrel
Squirrel searches and collects Linked Data

# Running with docker
```
  $ make build dockerize
  $ docker-compose build
  $ docker-compose up
```

## Dependencies
You need 2 additional repositories to (at least stay in the current version) the web component:

- [Squirrel-Webservice](https://github.com/phhei/Squirrel-Webservice)
  - ``mvn clean package``
  - please execute the ``buildImage.bat`` in the top directory layer
    - to use the local respository from the project, execute ``mvn install:install-file -DgroupId=org.aksw.simba.squirrel -DartifactId=SquirrelWebObject -Dpackaging=jar -Dversion=<currentVersion> -Dfile=<pathToSquirrelWebObject>\target\SquirrelWebObjectJar-<currentVersion>.jar -DgeneratePom=true -DlocalRepositoryPath=.\repository``
- [SquirrelWebObject](https://github.com/phhei/SquirrelWebObject)
  - ``mvn clean package``
  - please execute the ``install.bat`` in the top directory layer
