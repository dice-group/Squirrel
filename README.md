# Squirrel
Squirrel searches and collects Linked Data

# Running with docker
```
  $ make build dockerize
  $ docker-compose build
  $ docker-compose up
```

## Dependencies
You need 2 additional repositories to run the web component:

- [Squirrel-Webservice](https://github.com/phhei/Squirrel-Webservice)
  - ``mvn clean package``
  - please execute the ``buildImage.bat`` in the top directory layer
- [SquirrelWebObject](https://github.com/phhei/SquirrelWebObject)
  - ``mvn clean package``
  - please execute the ``install.bat`` in the top directory layer
