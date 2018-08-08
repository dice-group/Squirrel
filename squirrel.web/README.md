# Squirrel-Webservice

![SPRING logo](https://cdn.auth0.com/blog/spring-boot-auth/logo.png)

A Webservice for observing the [squirrel crawler](https://github.com/varunmaitreya/Squirrel). Based on SpringBoot :).

## Technical notes

### Dependencies
This project use the non public library SquirrelWebObject. Please execute first the ``SquirrelWebObject\install.bat`` of this project!

### ``buildImage.bat``

This file creates you a Docker image, that contains already all things that you need for a running Webservice. Only the port binding is missing (in comments). Run the web component of the [squirrel crawler](https://github.com/varunmaitreya/Squirrel) with _docker-compose-sparql-web.yml_.

## Manual

### How to access

Open the Browser of your choice (I hope, this is not Internet Explorer) und type in [http://localhost:8080](http://localhost:8080)


### What you can do

First, on the home page you can see the current status of squirrel. You can observe the stats (real time or you can navigate with the history slider to a certain point of the past) and you can have a look into the pending URIs or further URI connection. Please note, that you can't see the list of crawled URIs. Reason for that is the possible oversize of that list.

On the home apge, you can find on the right side a text field. Here you gave the possibility to add URIs to the Frontier::pending URIs.

On the other pages you can find further (static) information like information about the project group or the **visulaization of crawled graph**.

#### [Visualization of crawled graph](http://localhost:8080/observer/crawledGraph)

Means a graph, were you can find already crawled URIs with outgoing edges to URIs, that were found by crawling these and so forth. On the left side, you can find the older URIs, on the right side the later found URIs. Note, that your view is limited and for the reason of giving an overview URIs are reduced in the default config to their namespaces!

#### Further hidden links

- [Statistic backend](http://localhost:8080/observer/stat)
- and further easter eggs ;)
