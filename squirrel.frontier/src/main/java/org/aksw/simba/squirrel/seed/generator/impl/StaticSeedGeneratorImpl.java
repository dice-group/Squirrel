package org.aksw.simba.squirrel.seed.generator.impl;

import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticSeedGeneratorImpl extends AbstractSeedGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeedGenerator.class);

	public StaticSeedGeneratorImpl(Frontier frontier) {
		super(frontier);
	}

    @Override
	public List<CrawleableUri> getSeed() {
		String[] seedUris = {
				         "http://danbri.org/foaf.rdf", //RDF Dump
				         "http://rv1460.1blu.de/datasets/global-airports/global-airports.ttl", //RDF Dump
						 "http://rv2622.1blu.de:8890/sparql/", //SPARQL Endpoint
				 		 //"https://ckannet-storage.commondatastorage.googleapis.com/2015-06-10T11:58:16.954Z/zipped-dump.zip" //Zipped dump - folder with 4 files
		};
		return this.createCrawleableUriList(seedUris);
	}
}
