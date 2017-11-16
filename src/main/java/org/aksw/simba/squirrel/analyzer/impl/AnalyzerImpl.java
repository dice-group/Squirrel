package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyzerImpl implements Analyzer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzerImpl.class);

    private UriCollector collector;

    public AnalyzerImpl(UriCollector collector) {
        this.collector = collector;
    }

    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
        FileInputStream fin = null;
        try {

        	
        	
        	//uses default rdfxml language for the file.
        	
        	FilterSinkRDF filtered = new FilterSinkRDF() ;
    		RDFDataMgr.parse(filtered,data.getAbsolutePath(),Lang.RDFXML);
            Set<Triple> triples = filtered.getTriples();
        	
       
            Iterator<Triple> tripleIter = triples.iterator();
            Triple t;
            while (tripleIter.hasNext()) {
                t = tripleIter.next();
                sink.addTriple(curi, t);
                collector.addTriple(curi, t);
            }
        } catch (Exception e) {
            LOGGER.error("Exception while analyzing. Aborting.");
        } finally {
            IOUtils.closeQuietly(fin);
        }
        

        return collector.getUris(curi);
    }
    
    static class FilterSinkRDF extends StreamRDFBase
    {
        
        // Where to send the filtered triples.
        private Set<Triple> triples = new LinkedHashSet<>();

        @Override
        public void triple(Triple triple)
        {
                  triples.add(triple);
        }
        
        public Set<Triple> getTriples(){
        	return triples;        	
        }
        
    }

}
