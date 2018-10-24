package org.dice_research.squirrel.vocab;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.squirrel.Constants;

/**
 * A simple utility class in which we collected out predefined prefixes.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class Prefixes {

    public static final Map<String,String> PREFIX_TO_URI = generatePrefixMap();

    private static Map<String, String> generatePrefixMap() {
        Map<String, String> mapping = new HashMap<String,String>();
        mapping.put("dc", DCTerms.NS);
        mapping.put("dcat", DCAT.uri);
        mapping.put("owl", OWL2.NS);
        mapping.put("prov", PROV_O.uri);
        mapping.put("rdf", RDF.uri);
        mapping.put("rdfs", RDFS.uri);
        mapping.put("sq", Squirrel.uri);
        mapping.put("sq-a", Constants.DEFAULT_ACTIVITY_URI_PREFIX.toString());
        mapping.put("sq-g", Constants.DEFAULT_RESULT_GRAPH_URI_PREFIX.toString());
        mapping.put("sq-m", Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
        mapping.put("sq-s", Constants.DEFAULT_STATUS_URI_PREFIX.toString());
        mapping.put("sq-w", Constants.DEFAULT_WORKER_URI_PREFIX.toString());
        return Collections.unmodifiableMap(mapping);
    }
}
