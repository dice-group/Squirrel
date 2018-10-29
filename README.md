# Squirrel - Crawler of linked data.

## Introduction
Squirrel is a crawler for the linked web. It provides several tools to search and collect data
from the heterogeneous content of the linked web.

![Squirrel logo](https://hobbitdata.informatik.uni-leipzig.de/squirrel/squirrel-logo.png =248x244)

## Build notes
You can build the project with a simple ***mvn clean install***
and then you can use the *makefile*

```
  $ make build dockerize
  $ docker-compose build
  $ docker-compose up
```

## Run
You can run by using the docker-compose file.

```
  $ docker-compose -f docker-compose-sparql.yml up
```

Squirrel uses spring context configuration to define the implementation of its components in Runtime.
you can check the default implementation file in spring-config/sparqlStoreBased.xml and define your own
beans on it.

You can also define a different context for each one of the workers. Check the docker-compose file and change
an implementation file in each worker's env variable.

These are the components of Squirrel that can be customized:

#### Fetcher

* *HTTPFetcher* - Fetches data from html sources.
* *FTPFetcher* - Fetches data from html sources.
* *SparqlBasedFetcher* - Fetches data from Sparql endpoints.

* *Note*: The fetchers are not managed as spring beans yet, since only three are available.

#### Analyzer
Analyses the fetched data and extract triples from it. Note: the analyzer implementations are managed by the `SimpleAnalyzerManager`. Any implementations should be passed in the constructor of this class, like the example below:
```xml
<bean id="analyzerBean" class="org.aksw.simba.squirrel.analyzer.manager.SimpleAnalyzerManager">
        <constructor-arg index="0" ref="uriCollectorBean" />
        <constructor-arg index="1" >
        	<array value-type="java.lang.String">
			  <value>org.aksw.simba.squirrel.analyzer.impl.HDTAnalyzer</value>
			  <value>org.aksw.simba.squirrel.analyzer.impl.RDFAnalyzer</value>
			  <value>org.aksw.simba.squirrel.analyzer.impl.HTMLScraperAnalyzer</value>
		</array>
       	</constructor-arg>
</bean>
```
Also, if you want to implement your own analyzer, it is necessary to implement the method `isEligible()`, that checks if that analyzer matches the condition to call the `analyze` method.

* *RDFAnalyzer* - Analyses RDF formats.
* *HTMLScraperAnalyzer* - Analyses and scrapes HTML data base on Jsoup selector-synthax (see: https://github.com/dice-group/Squirrel/wiki/HtmlScraper_how_to)
* *HDTAnalyzer* - Analyses HDT binary RDF format.

#### Collectors
Collects new URIs found during the analysis process and serialize it before they are sent to the Frontier.

* *SimpleUriCollector* - Serialize uri's and stores it in memory (mainly used for testing purposes).
* *SqlBasedUriCollector* - Serialize uri's and stores it in a hsqldb database.

#### Sink
Responsible for persisting the collected RDF data.

* *FileBasedSink* - persists the triples in NT files,
* *InMemorySink* - persists the triples only in memory, not in disk (mainly used for testing purposes).
* *HdtBasedSink* - persists the triples in a HDT file (compressed RDF format - http://www.rdfhdt.org/).
* *SparqlBasedSink* - persists the triples in a SparqlEndPoint.


