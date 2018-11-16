package org.dice_research.squirrel.analyzer;

import org.dice_research.squirrel.collect.UriCollector;

/**
 * Abstract class to define a constructor
 * and UriCollector for analyzers.
 * 
 * @author gsjunior - gsjunior@mail.uni-paderborn.de
 *
 */
public abstract class AbstractAnalyzer implements Analyzer{
	
	protected UriCollector collector;
	
	public AbstractAnalyzer(UriCollector collector) {
		this.collector = collector;
	}

}
