# Squirrel
Squirrel searches and collects Linked Data

# Running with docker
```
  $ make build dockerize
  $ docker-compose build
  $ docker-compose up
```

## Using a Sparql-Host

You can use a sparql-based triple store to store the crawled. If you want use it, you have to do the following:

Until yet, the necessary datasets in the Jena Database are not created automatically. So you have to create them by hand:
1. Run Squirrel as explained above 
2. Enter *localhost:3030* in your browser's address line
3. Go to *manage datasets*
4. Click *add new dataset*
5. For *Dataset name* paste *ContentSet*
6. For *Dataset type* select *Persistent – dataset will persist across Fuseki restarts*
7. Go to step 4 again and do the same, **but this time with *"Metadata"* as *"Dataset name"***

## Dependencies
You need 2 additional repositories to (at least stay in the current version) the web component:

- [Squirrel-Webservice](https://github.com/phhei/Squirrel-Webservice)
  - ``mvn clean package``
  - please execute the ``buildImage.bat`` in the top directory layer
    - to use the local respository from the project, execute ``mvn install:install-file -DgroupId=org.aksw.simba.squirrel -DartifactId=SquirrelWebObject -Dpackaging=jar -Dversion=<currentVersion> -Dfile=<pathToSquirrelWebObject>\target\SquirrelWebObjectJar-<currentVersion>.jar -DgeneratePom=true -DlocalRepositoryPath=.\repository``
- [SquirrelWebObject](https://github.com/phhei/SquirrelWebObject)
  - ``mvn clean package``
  - please execute the ``install.bat`` in the top directory layer

