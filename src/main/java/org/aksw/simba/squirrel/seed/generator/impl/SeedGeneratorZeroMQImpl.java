package org.aksw.simba.squirrel.seed.generator.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.seed.generator.SeedGeneratorZeroMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeedGeneratorZeroMQImpl implements SeedGeneratorZeroMQ {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SeedGeneratorZeroMQImpl.class);
	private Frontier frontier;
	
	public SeedGeneratorZeroMQImpl(Frontier frontier) {
		this.frontier = frontier;
	}

    public void run() {
		LOGGER.debug("Started Seed Generator...");
		this.sendStaticSeed();
    }
	
    @Override
	public void sendStaticSeed() {
		LOGGER.debug("Sending static seed to frontier...");
		frontier.addNewUris(this.getStaticSeed());
	}
	
    @Override
	public List<CrawleableUri> getStaticSeed() {
		String[] seedUris = {"http://danbri.org/foaf.rdf",
				         "http://mmt.me.uk/foaf.rdf#mischa",
				         "http://www.ils.unc.edu/~janeg/foaf.rdf#me"};
		CrawleableUriFactoryImpl crawleableUriFactoryImpl = new CrawleableUriFactoryImpl();
		java.util.List<CrawleableUri> seed;
		seed = new ArrayList<CrawleableUri>();
		for(int i=0; i<seedUris.length; i++) {
			seed.add(crawleableUriFactoryImpl.create(seedUris[i]));
		}
		
		return seed;
	}
}
