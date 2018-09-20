package org.aksw.simba.squirrel;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
    public static final String URI_CRAWLING_ACTIVITY = "activity";

    public static final String URI_HASH_KEY = "HashValue";
    public static final String UUID_KEY = "UUID";

    /**
     * The preferred date for recrawling a URI is assumed to be a timestamp (in ms
     * from 1st January 1970).
     */
    public static final String URI_PREFERRED_RECRAWL_ON = "recrawl-on";

    //////////////////////////////////////////////////
    // URIs
    //////////////////////////////////////////////////

    public static final String SQUIRREL_URI_PREFIX = "http://w3id.org/squirrel";
    public static final URI DEFAULT_META_DATA_GRAPH_URI = URI.create(SQUIRREL_URI_PREFIX + "/metadata");
    public static final URI DEFAULT_ACTIVITY_URI_PREFIX = URI.create(SQUIRREL_URI_PREFIX + "/activity#");
    public static final URI DEFAULT_RESULT_GRAPH_URI_PREFIX = URI.create(SQUIRREL_URI_PREFIX + "/graph#");
    public static final URI DEFAULT_STATUS_URI_PREFIX = URI.create(SQUIRREL_URI_PREFIX + "/status#");
    public static final URI DEFAULT_WORKER_URI_PREFIX = URI.create(SQUIRREL_URI_PREFIX + "/worker#");

    //////////////////////////////////////////////////
    // Component constants
    //////////////////////////////////////////////////
    
    public static final String DEDUPLICATION_ACTIVE_KEY = "DEDUPLICATION_ACTIVE";

    public static final String RDB_HOST_NAME_KEY = "RDB_HOST_NAME";
    
    public static final String RDB_PORT_KEY = "RDB_PORT";

    public static final boolean DEFAULT_DEDUPLICATION_ACTIVE = false;

    public static final String DEDUPLICATOR_QUEUE_NAME = "squirrel.deduplicator";
    public static final String FRONTIER_QUEUE_NAME = "squirrel.frontier";

    //////////////////////////////////////////////////
    // General constants
    //////////////////////////////////////////////////

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_USER_AGENT = "Squirrel";
}
