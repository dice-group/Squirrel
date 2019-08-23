package org.dice_research.squirrel.analyzer.commons;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Triple handler for Clerezza
 * 
 * @author Geraldo de Souza Junior
 *
 */

public class SquirrelTripleHandler implements TripleHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SquirrelTripleHandler.class);

	
    private final UriCollector collector;
    private final Sink sink;
    private final CrawleableUri curi;
    
    public SquirrelTripleHandler(CrawleableUri curi, UriCollector collector,Sink sink) {
		this.collector = collector;
		this.sink = sink;
		this.curi = curi;
	}

	@Override
	public void startDocument(IRI documentIRI) throws TripleHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openContext(ExtractionContext context) throws TripleHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context)
			throws TripleHandlerException {
		boolean isUri1 = true;
		boolean isUri2 = true;

		
		if(isUri2) {
			Node r = NodeFactory.createURI(s.stringValue());
			Node pr = NodeFactory.createURI(p.toString());
			Node obj = isUri1 ? NodeFactory.createURI(o.stringValue()) : NodeFactory.createLiteral(o.stringValue());
			
			Triple t = new Triple(r,pr,obj);
			
			sink.addTriple(curi, t);
			collector.addTriple(curi, t);
		}
		
	
		
	}

	@Override
	public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeContext(ExtractionContext context) throws TripleHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endDocument(IRI documentIRI) throws TripleHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentLength(long contentLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws TripleHandlerException {
		LOGGER.info(">> Closing SquirrelTripleWriter for URI: " + curi.getUri().toString());
	}

	


}
