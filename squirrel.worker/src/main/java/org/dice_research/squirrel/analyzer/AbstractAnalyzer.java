package org.dice_research.squirrel.analyzer;

import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.encoder.TripleEncoder;

/**
 * Abstract class to define a constructor
 * and UriCollector for analyzers.
 * 
 * @author gsjunior - gsjunior@mail.uni-paderborn.de
 *
 */
public abstract class AbstractAnalyzer implements Analyzer{
	
	protected UriCollector collector;
	
	protected TripleEncoder tripleEncoder = new TripleEncoder();
	
	public AbstractAnalyzer(UriCollector collector) {
		this.collector = collector;
	}
	

}
