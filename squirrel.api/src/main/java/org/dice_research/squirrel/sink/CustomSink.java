package org.dice_research.squirrel.sink;

import java.io.InputStream;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.dice_research.squirrel.data.uri.CrawleableUri;

public class CustomSink implements Sink{

	@Override
	public void addTriple(CrawleableUri uri, Triple triple) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openSinkForUri(CrawleableUri uri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeSinkForUri(CrawleableUri uri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addQuad(CrawleableUri uri, Quad quad) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addData(CrawleableUri uri, InputStream stream) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushMetadata() {
		// TODO Auto-generated method stub
		
	}

}
