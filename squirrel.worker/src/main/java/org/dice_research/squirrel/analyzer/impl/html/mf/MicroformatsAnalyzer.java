package org.dice_research.squirrel.analyzer.impl.html.mf;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.dice_research.squirrel.sink.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kylewm.mf2j.Mf2Parser;

import eu.trentorise.opendata.traceprov.internal.org.apache.commons.io.FileUtils;

public class MicroformatsAnalyzer implements Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicroformatsAnalyzer.class);

    protected static final String PROPERTY_MAPPING[][] = new String[][] {
            { "http://www.w3.org/2006/vcard/ns#count", "http://purl.org/stuff/revagg#count" },
            { "http://www.w3.org/2006/vcard/ns#average", "http://purl.org/stuff/revagg#average" },
            { "http://www.w3.org/2006/vcard/ns#best", "http://purl.org/stuff/revagg#best" },
            { "http://www.w3.org/2006/vcard/ns#rating", "http://purl.org/stuff/rev#rating" },
            { "http://www.w3.org/2006/vcard/ns#country-name", "http://purl.org/stuff/revagg#country-name" },
            { "http://www.w3.org/2006/vcard/ns#type", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" },
            { "http://www.w3.org/2006/vcard/ns#location", "http://www.w3.org/2002/12/cal/icaltzd#location" },
            { "http://www.w3.org/2006/vcard/ns#start", "http://www.w3.org/2002/12/cal/icaltzd#dtstart" },
            { "http://www.w3.org/2006/vcard/ns#end", "http://www.w3.org/2002/12/cal/icaltzd#dtend" },
            { "http://www.w3.org/2006/vcard/ns#url", "http://www.w3.org/2002/12/cal/icaltzd#url" },
            { "http://www.w3.org/2006/vcard/ns#org", "http://www.w3.org/2006/vcard/ns#organization-name" },
            { "http://www.w3.org/2006/vcard/ns#affiliation", "http://ramonantonio.net/doac/0.1/#affiliation" },
            { "http://www.w3.org/2006/vcard/ns#summary", "http://ramonantonio.net/doac/0.1/#summary" },
            { "http://www.w3.org/2006/vcard/ns#education", "http://ramonantonio.net/doac/0.1/#education" },
            { "http://www.w3.org/2006/vcard/ns#experience", "http://ramonantonio.net/doac/0.1/#experience" },
            { "http://www.w3.org/2006/vcard/ns#job-title", "http://ramonantonio.net/doac/0.1/#title" },
            { "http://www.w3.org/2006/vcard/ns#license", "http://www.w3.org/1999/xhtml/vocab#license" } };

    private UriCollector collector;
    protected Mf2Parser parser;

    public MicroformatsAnalyzer(UriCollector collector, Mf2Parser parser) {
        this.collector = collector;
        this.parser = parser;
    }

    public MicroformatsAnalyzer(UriCollector collector) {
        this(collector, new Mf2Parser().setIncludeAlternates(true).setIncludeRelUrls(true));
    }

    @Override
    public Iterator<byte[]> analyze(CrawleableUri curi, File data, Sink sink) {
        Map<String, Object> parsed;
        try {
            parsed = parser.parse(FileUtils.readFileToString(data), URI.create(curi.getUri().toString()));
            String json = addContextToJSON(parsed.toString());
            json = replaceVocab(json);
            // System.out.println(json);
            Model model = createModelFromJSONLD(json);
            String syntax = "N-TRIPLE";
            StringWriter out = new StringWriter();
            model.write(out, syntax);
            String result = out.toString();
            sink.addData(curi, result);

            ActivityUtil.addStep(curi, getClass());
            return collector.getUris(curi);
        } catch (Exception e) {
            LOGGER.error("Exception while analyzing file of URI \"" + curi.getUri().toString() + "\"", e);
            ActivityUtil.addStep(curi, getClass(), e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isElegible(CrawleableUri curi, File data) {
        String contentType = (String) curi.getData(Constants.URI_HTTP_MIME_TYPE_KEY);
        return ((contentType != null && contentType.equals("text/html")));
    }

    public static String addContextToJSON(String data) {
        return "{\"@context\": {\"@vocab\": \"http://www.dummy.org/#\"}," + data.trim().substring(1);
    }

    public static String replaceVocab(String data) {
        return data.replace("http://www.dummy.org/#", "http://www.w3.org/2006/vcard/ns#");
    }

    /**
     * Creates a Model from JSON-LD
     * 
     * @param content
     *            the data in JSON-LD
     * @return the model
     */
    public static Model createModelFromJSONLD(String content) {
        Model model = null;
        try {
            model = ModelFactory.createDefaultModel().read(IOUtils.toInputStream(content, "UTF-8"), null, "JSON-LD");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }
}
