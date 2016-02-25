package org.aksw.simba.squirrel.seed.generator.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
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
		String[] seedUris = {"http://danbri.org/foaf.rdf",
				         "http://mmt.me.uk/foaf.rdf#mischa",
				         "http://www.ils.unc.edu/~janeg/foaf.rdf#me"};
		return this.createCrawleableUriList(seedUris);
	}
}
