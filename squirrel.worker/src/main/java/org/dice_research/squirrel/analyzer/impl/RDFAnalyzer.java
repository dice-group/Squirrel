package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.tika.io.IOUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.AbstractAnalyzer;
import org.dice_research.squirrel.analyzer.commons.FilterSinkRDF;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.dice_research.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Analyzer to parse RDF lang types
 * 
 * 
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 */

public class RDFAnalyzer extends AbstractAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDFAnalyzer.class);

    private List<Lang> listLangs = new ArrayList<Lang>();
    private Set<String> jenaContentTypes = new HashSet<String>();


    public RDFAnalyzer(UriCollector collector) {
        
    	super(collector);
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
        
        for (Lang lang : RDFLanguages.getRegisteredLanguages()) {
            if (!RDFLanguages.RDFNULL.equals(lang)) {
                jenaContentTypes.add(lang.getContentType().getContentType());
                jenaContentTypes.addAll(lang.getAltContentTypes());
            }
        }
    }

    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
        FileInputStream fin = null;
        try {
            // First, try to get the language of the data
            LOGGER.info("Starting the RDF Analyzer");
            Lang lang = null;
            String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
            StreamRDF filtered = new FilterSinkRDF(curi, sink, collector);
            if (contentType != null) {
                lang = RDFLanguages.contentTypeToLang(contentType);
                LOGGER.info("Received content type: " + contentType);
                RDFDataMgr.parse(filtered, data.getAbsolutePath(), lang);
            } else {
                for (Lang l : listLangs) {
                    try {
                        RDFDataMgr.parse(filtered, data.getAbsolutePath(), l);
                        break;
                    } catch (Exception e) {
                        LOGGER.warn("Could not parse file as " + l.getName());
                    }
                }
            }
            ActivityUtil.addStep(curi, getClass());
            return collector.getUris(curi);
        } catch (Exception e) {
            LOGGER.error("Exception while analyzing. Aborting. ", e);
            ActivityUtil.addStep(curi, getClass(), e.getMessage());
            return null;
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }

//    @Override
    public boolean isElegible(CrawleableUri curi, File data) {
        // Check the content type first
        String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);

        
        if ((contentType != null) && jenaContentTypes.contains(contentType)) {
            return true;
        }
//        // Try to get the tika mime type
//        // TODO it might be better to do that once and add it to the URIs data
//        try (InputStream is = new FileInputStream(data)) {
//            String mimeType = tika.detect(is);
//            if (mimeType.equals("application/rdf+xml") || mimeType.equals("text/plain")
//                    || mimeType.equals("application/x-turtle")) {
//                return true;
//            }
//
//        } catch (Exception e) {
//            LOGGER.error("An error was found when trying to analyze ", e);
//        }
        return false;
    }

}
