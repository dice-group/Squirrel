package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.Quad;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFAnalyzer implements Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDFAnalyzer.class);

    private UriCollector collector;

    private List<Lang> listLangs = new ArrayList<Lang>();



    public RDFAnalyzer(UriCollector collector) {
        this.collector = collector;
        listLangs.add(Lang.NT);
        listLangs.add(Lang.NQUADS);
        listLangs.add(Lang.RDFJSON);
        listLangs.add(Lang.RDFTHRIFT);
        listLangs.add(Lang.RDFXML);
        listLangs.add(Lang.JSONLD);
        listLangs.add(Lang.TRIG);
        listLangs.add(Lang.TRIX);
        listLangs.add(Lang.TTL);
        listLangs.add(Lang.TURTLE);
    }

    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
        try {
            // First, try to get the language of the data
            Lang lang = null;
            String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
            StreamRDF filtered = new FilterSinkRDF(curi, sink, collector);
            if (contentType != null ) {
                lang = RDFLanguages.contentTypeToLang(contentType);
                RDFDataMgr.parse(filtered, data.getAbsolutePath(), lang);
            } else {
            	for(Lang l : listLangs) {
            		try {
            			System.out.println(data.getAbsolutePath());
            			RDFDataMgr.parse(filtered, data.getAbsolutePath(), l);
            			break;
            		}catch(Exception e) {

            			LOGGER.warn("Could not parse file as " + l.getName());
            		}

            	}

//                InputStream is = new FileInputStream(data);
//                lang = RDFLanguages.contentTypeToLang(tika.detect(is));
//                try {
//                	RDFDataMgr.parse(filtered, data.getAbsolutePath(), lang);
//                }catch(Exception e) {
//                	if(Lang.NTRIPLES.equals(lang)) {
//	                	LOGGER.warn("Could not parse file as N-Triples. Trying N-Quads...");
//	                	RDFDataMgr.parse(filtered, data.getAbsolutePath(), Lang.N3);
//	                } else
//	                {
//	                	throw e;
//	                }
//                }finally {
//					is.close();
//				}
            }


        } catch (Exception e) {
            LOGGER.error("Exception while analyzing. Aborting. ", e);
        }

        return collector.getUris(curi);
    }

    protected class FilterSinkRDF extends StreamRDFBase {

        private CrawleableUri curi;
        private Sink sink;
        private UriCollector collector;

        public FilterSinkRDF(CrawleableUri curi, Sink sink, UriCollector collector) {
            this.curi = curi;
            this.sink = sink;
            this.collector = collector;
        }

        @Override
        public void triple(Triple triple) {
        	sink.addTriple(curi, triple);
            collector.addTriple(curi, triple);
        }

        @Override
        public void quad(Quad quad) {
        	sink.addTriple(curi, quad.asTriple());
            collector.addTriple(curi, quad.asTriple());
        }

    }

}
