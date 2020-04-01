# Squirrel - Crawler of linked data.

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e98f6dbb54c548ab868f3656c7e6f674)](https://app.codacy.com/app/MichaelRoeder/Squirrel?utm_source=github.com&utm_medium=referral&utm_content=dice-group/Squirrel&utm_campaign=Badge_Grade_Dashboard)

## Introduction
Squirrel is a crawler for the linked web. It provides several tools to search and collect data
from the heterogeneous content of the linked web.

You can find the crawler documentation, tutorials and more here:
<https://dice-group.github.io/squirrel.github.io/>

<img src="https://hobbitdata.informatik.uni-leipzig.de/squirrel/squirrel-logo.png" align="center" height="248" width="244" > 



## Run

Clone the repository in a folder of your choice with:

```
	$ git clone https://github.com/dice-group/Squirrel
```


Enter into the Squirrel folder and initialize rabbitmq and mongodb images:

```
  $ docker-compose up -d mongodb rabbit

```

Set up your seeds on the file seed/seeds.txt and start the frontier and one worker instance with:

```
  $ docker-compose up frontier worker1

```

Check the official web site for documentation and tutorials:
