package org.aksw.simba.squirrel;

import java.net.URI;
import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;
import org.apache.http.HttpHeaders;

/**
 * This class contains constant values of the Squirrel project.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class Constants {

    //////////////////////////////////////////////////
    // URI data keys
    //////////////////////////////////////////////////
    public static final String URI_TYPE_KEY = "type";
    public static final String URI_TYPE_VALUE_DEREF = "dereferenceable";
    public static final String URI_TYPE_VALUE_SPARQL = "sparql";
    public static final String URI_TYPE_VALUE_DUMP = "rdf-dump";
    public static final String URI_TYPE_VALUE_CSV = "csv";
    public static final String URI_TYPE_VALUE_HTML = "html";

    public static final String URI_DATA_FILE_NAME = "data-file-name";

    public static final String URI_HTTP_MIME_TYPE_KEY = "http-mime-type";
    public static final String URI_HTTP_CHARSET_KEY = "http-charset";
    public static final String URI_HTTP_STATUS_CODE = "http-status";

    public static final String URI_HTTP_ACCEPT_HEADER = "http-" + HttpHeaders.ACCEPT;
    public static final String URI_HTTP_ACCEPT_CHARSET_HEADER = "http-" + HttpHeaders.ACCEPT_CHARSET;
    
    public static final String URI_CRAWLING_ACTIVITY_URI = "activity-uri";

    /**
     * The preferred date for recrawling a URI is assumed to be a timestamp (in ms
     * from 1st January 1970).
     */
    public static final String URI_PREFERRED_RECRAWL_ON = "recrawl-on";

    //////////////////////////////////////////////////
    // URIs
    //////////////////////////////////////////////////

    public static final URI DEFAULT_META_DATA_GRAPH_URI = URI.create("http://squirrel.dice-research.org/vocab#metadata");

    //////////////////////////////////////////////////
    // General constants
    //////////////////////////////////////////////////

    public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;
}
