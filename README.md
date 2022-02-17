[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e98f6dbb54c548ab868f3656c7e6f674)](https://app.codacy.com/app/MichaelRoeder/Squirrel?utm_source=github.com&utm_medium=referral&utm_content=dice-group/Squirrel&utm_campaign=Badge_Grade_Dashboard)

# Squirrel - Crawler of linked data

## Introduction
Squirrel is a crawler for the linked web. It provides several tools to search and collect data
from the heterogeneous content of the linked web.

Documentation, tutorials and more: <https://dice-group.github.io/squirrel.github.io/>

<img src="https://hobbitdata.informatik.uni-leipzig.de/squirrel/squirrel-logo.png" align="center" height="248" width="244" > 

## Requirements

### Building
- Java 1.8
- Apache Maven 3.6.0
- Docker 19.03.12

### Running
- Docker 19.03.12

or

- ORCA benchmark on the HOBBIT platform

## How to run

Clone the repository in a directory of your choice with:

```sh
git clone https://github.com/dice-group/Squirrel
```


Enter into the Squirrel directory and start RabbitMQ and MongoDB containers:

```sh
docker-compose up -d mongodb rabbit
```

Set up your seeds in the file `seed/seeds.txt` and start the frontier and one worker instance with:

```sh
docker-compose up frontier worker1
```

## Publications

### Squirrel – Crawling RDF Knowledge Graphs on the Web

https://www.bibsonomy.org/bibtex/29fe2ef0c2e1908276d424c1ca3e06cbf/dice-research

#### Reproducing results

1. Go to https://master.project-hobbit.eu/
2. Register an account or log in into an existing one
3. Go to "Benchmarks"
4. Select "ORCA" in the Benchmark list
5. Select the system and set all parameters (also can be found by following links in the paper):

Parameter                                    | Effectiveness | Efficiency
-------------------------------------------- | ------------- | ----------
Average crawl delay                          | 0             | 0
Average node degree                          | 20            | 20
Average ratio of disallowed resources        | 0             | 0
Average resource degree                      | 9             | 9
Disallowed resources                         | 0             | 0
Dump file compression ratio                  | **0.3**       | **0**
Node size definition                         | Static        | Static
Number of nodes                              | **100**       | **200**
RDF dataset size                             | 1000          | 1000
Seed                                         | 20200318      | 20200318
Use N3 dumps                                 | true          | true
Use NT dumps                                 | true          | true
Use RDF/XML dumps                            | true          | true
Use TTL dumps                                | true          | true
Weight of CKAN node occurrence               | **5**         | **0**
Weight of dereferencing HTTP node occurrence | **21**        | **100**
Weight of HTTP dump file node occurrence     | **40**        | **0**
Weight of RDFa node occurrence               | **4**         | **0**
Weight of SPARQL node occurrence             | **30**        | **0**

6. Use "Submit" to queue the experiment
7. Watch the received link for experiment results. You can use "Experiments → Experiment Status" page to check if it's still running.

It is also possible to deploy your own HOBBIT platform. Refer to the HOBBIT platform manual: <https://hobbit-project.github.io/>. In this case you may need system adapters for ORCA as well: <https://github.com/topics/orca-system-adapter>.
