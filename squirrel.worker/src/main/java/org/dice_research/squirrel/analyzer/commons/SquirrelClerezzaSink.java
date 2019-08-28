package org.dice_research.squirrel.analyzer.commons;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.clerezza.rdf.core.BNode;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.semarglproject.rdf.ParseException;
import org.semarglproject.sink.TripleSink;
import org.semarglproject.vocab.RDF;


/**
 * 
 * TripleSink for Clerezza
 * 
 * @author Geraldo de Souza Junior
 *
 */

public class SquirrelClerezzaSink implements TripleSink {

	public static final String OUTPUT_GRAPH_PROPERTY = "http://semarglproject.org/clerezza/properties/output-graph";

	private CrawleableUri curi;
	private UriCollector collector;
	private Sink sink;
	private final Map<String, BNode> bnodeMap;

	protected SquirrelClerezzaSink(CrawleableUri curi, UriCollector collector, Sink sink) {
		this.curi = curi;
		this.sink = sink;
		this.collector = collector;
				
		bnodeMap = new HashMap<String, BNode>();
	}
	
	  public static TripleSink connect(CrawleableUri curi,UriCollector collector, Sink sink) {
	        return new SquirrelClerezzaSink(curi,collector,sink);
	    }

	private BNode getBNode(String bnode) {
		if (!bnodeMap.containsKey(bnode)) {
			bnodeMap.put(bnode, new BNode());
		}
		return bnodeMap.get(bnode);
	}

	@SuppressWarnings("unused")
	private NonLiteral convertNonLiteral(String arg) {
		if (arg.startsWith(RDF.BNODE_PREFIX)) {
			return getBNode(arg);
		}
		return new UriRef(arg);
	}

	@Override
	public final void addNonLiteral(String subj, String pred, String obj) {
		addTriple(subj,pred,obj);
	}

	@Override
	public final void addPlainLiteral(String subj, String pred, String content, String lang) {
		addTriple(subj,pred,content);
	}

	@Override
	public final void addTypedLiteral(String subj, String pred, String content, String type) {
		addTriple(subj,pred,content);
	}

	@Override
	public boolean setProperty(String key, Object value) {
		return (OUTPUT_GRAPH_PROPERTY.equals(key) && value instanceof MGraph);
	}

	/**
	 * Callback method for handling Clerezza triples.
	 * 
	 * @param subj triple's subject
	 * @param pred triple's predicate
	 * @param obj  triple's object
	 */
	protected void addTriple(String subj, String pred, String obj) {
		boolean resourceUrl = true;
		try {
			@SuppressWarnings("unused")
			URL url = new URL(subj);
		} catch (MalformedURLException e) {
			resourceUrl = false;
		}
		
		boolean objUrl = true;
		try {
			@SuppressWarnings("unused")
			URL url = new URL(obj);
		} catch (MalformedURLException e) {
			objUrl = false;
		}
		
		if(resourceUrl) {
			Node s = NodeFactory.createURI(subj);
			Node o = NodeFactory.createURI(pred);
			Node p = objUrl ? NodeFactory.createURI(obj) : NodeFactory.createLiteral(obj);
		
			Triple t = new Triple(s,o,p);
			sink.addTriple(curi, t);
			collector.addTriple(curi, t);
		}

	}

	@Override
	public void startStream() throws ParseException {
	    //not used on this implementation
	}

	@Override
	public void endStream() throws ParseException {
	       //not used on this implementation
	}

	@Override
	public void setBaseUri(String baseUri) {
	       //not used on this implementation
	}
}