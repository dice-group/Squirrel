package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AnalyzerTest {
	
	private URI uri;
	private Sink sink;
	private Model model;
	
	
	public AnalyzerTest(URI uri, Sink sink, Model model) {
		this.uri = uri;
		this.sink = sink;
		this.model = model;
	}
	
	@Parameters
	public static Collection<Object[]> data() throws Exception {
		
		
		 return Arrays.asList(new Object[][] { {
			new URI("http://pdb.de/") , new FileBasedSink(new File("/home/gsjunior/test_folder/"), false) , ModelFactory.createDefaultModel().read("http://danbri.org/foaf.rdf")
		 } });
	}
	
	@Test
	public void test() {
		
		CrawleableUri curi = new CrawleableUri(uri);
		sink.openSinkForUri(curi);
		
		try {
			
	        StmtIterator si;
	        si = model.listStatements();

	        while(si.hasNext()) {
	            Statement s=si.nextStatement();
//	            Resource r=s.getSubject();
//	            Property p=s.getPredicate();
	            RDFNode o=s.getObject();
//	            sink.addTriple(curi, s.asTriple());
	            	            
//	            System.out.println(r.getURI());
//	            System.out.println(p.getURI());
	            System.out.println(o.asResource().getURI());
	        }
	    }
	    catch(JenaException | NoSuchElementException c) {}
		finally {
//			sink.closeSinkForUri(curi);
		}
	}

}
