package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.tika.Tika;
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
            LOGGER.info("Starting the RDF Analyzer for URI: " + curi.getUri().toString());
            Lang lang = null;
            Object httpMimeTypeObject = curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
            String contentType = null;
            // Make sure the mime type is available before using it AND check that it is not
            // text/plain (the latter is a workaround)
            if ((httpMimeTypeObject != null) && (!"text/plain".equals(httpMimeTypeObject.toString()))) {
                contentType = httpMimeTypeObject.toString();
            }
            StreamRDF filtered = new FilterSinkRDF(curi, sink, collector);
            if (contentType != null) {
                lang = RDFLanguages.contentTypeToLang(contentType);

                try {
                    RDFDataMgr.parse(filtered, data.getAbsolutePath(), lang);
                } catch (Exception e) {
                    LOGGER.warn("Could not parse file as " + lang.getName());
                }
            } else {
                LOGGER.info("Content Type is null");
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
            if(curi.getData(Constants.URI_TRUE_CLASS) == null){
                curi.addData(Constants.URI_TRUE_CLASS, "DUMP");
            }
            return collector.getUris(curi);
        } catch (Exception e) {
            LOGGER.error("Exception while analyzing. Aborting. ", e);
            ActivityUtil.addStep(curi, getClass(), e.getMessage());
            return null;
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }

    // @Override
    public boolean isElegible(CrawleableUri curi, File data) {
        // Check the content type first
        String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
        Tika tika = new Tika();

        if ("*/*".equals(contentType) || "text/plain".equals(contentType)) {
            try {
                contentType = tika.detect(data);
                curi.addData(Constants.URI_HTTP_MIME_TYPE_KEY, contentType);

            } catch (IOException e) {
                LOGGER.info("Could not Detect Mimetype using Tika, using from Fetcher");
            }
        }

        if ((contentType != null) && jenaContentTypes.contains(contentType))
            return true;
        else
            return false;
    }

}
