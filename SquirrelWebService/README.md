# Squirrel-Webservice
A Webservice for observing the [squirrel crawler](https://github.com/varunmaitreya/Squirrel). Based on SpringBoot :).

## Dependencies
This project use the non public library [SquirrelWebObject](https://github.com/phhei/SquirrelWebObject). Please execute first the ``install.bat`` of this project!

## ``buildImage.bat``

This file creates you a Docker image, that contains already all things that you need for a running Webservice. Only the port binding is missing (in comments). Please execute this bat **after** a ``mvn clean package`` to run the web component of the [squirrel crawler](https://github.com/varunmaitreya/Squirrel).