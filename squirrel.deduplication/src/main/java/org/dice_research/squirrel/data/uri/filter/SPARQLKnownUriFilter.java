package org.dice_research.squirrel.data.uri.filter;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.sink.SparqlBasedSinkDedup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class SPARQLKnownUriFilter implements KnownUriFilter, Closeable, UriHashCustodian {

    /*
   Some constants for the rethinkDB
    */
    public static final String DATABASE_NAME = "squirrel";
    public static final String TABLE_NAME = "knownurifilter";
    public static final String COLUMN_TIMESTAMP_LAST_CRAWL = "timestampLastCrawl";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_CRAWLING_IN_PROCESS = "crawlingInProcess";
    public static final String COLUMN_TIMESTAMP_NEXT_CRAWL = "timestampNextCrawl";
    public static final String COLUMN_IP = "ipAddress";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_HASH_VALUE = "hashValue";

    public static final String COLUMN_PREDICATE = "sq:hashvalue";

    private static final Logger LOGGER = LoggerFactory.getLogger(SPARQLKnownUriFilter.class);

    public SparqlBasedSinkDedup connector = null;

    public SPARQLKnownUriFilter(String sparqlEndpointUrl, String username, String password) {
        this.connector = SparqlBasedSinkDedup.create(sparqlEndpointUrl, username, password);
    }

    public List<String> getGeneratedUris(QueryExecutionFactory queryExecFactory){
        String queryString = "SELECT ?s ?p ?o\n" +
                "WHERE {\n" +
                " GRAPH <http://w3id.org/squirrel/metadata> {?s prov:value ?o}\n" +
                "}";
        LOGGER.info("Query looks like: ", queryString);

        QueryExecution qe = queryExecFactory.createQueryExecution(queryString);
        LOGGER.warn("Query execution: ", qe);

        ResultSet rs = qe.execSelect();
        List<String> urisFound = new ArrayList<>();
        System.out.println("-------------------------------------------------------------------------------------------------------");
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode object = sol.get("object");
            urisFound.add(object.asLiteral().toString());
        }
        qe.close();
        return urisFound;
    }
    @Override
    public void close() throws IOException {

    }

    @Override
    public void add(CrawleableUri uri, long nextCrawlTimestamp) {

    }

    @Override
    public void add(CrawleableUri uri, long lastCrawlTimestamp, long nextCrawlTimestamp) {

    }

    @Override
    public List<CrawleableUri> getOutdatedUris() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        return false;
    }

    @Override
    public Set<CrawleableUri> getUrisWithSameHashValues(Set<HashValue> hashValuesForComparison) {
        Set<String> stringHashValues = new HashSet<>();
        for (HashValue value : hashValuesForComparison) {
            stringHashValues.add(value.encodeToString());
        }

//        Cursor<HashMap> cursor = r.db(DATABASE_NAME).table(TABLE_NAME).filter
//                (doc -> stringHashValues.contains(doc.getField(COLUMN_HASH_VALUE))).run(connector.connection);

        List<Triple> triplesFound = new ArrayList<>();
        Set<CrawleableUri> urisToReturn = new HashSet<>();
        //TODO: Query the database again to check
        String tempQuery = "";
        QueryExecution qe = connector.queryExecFactory.createQueryExecution(tempQuery);
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode subject = sol.get("subject");
            RDFNode predicate = sol.get("predicate");
            RDFNode object = sol.get("object");

            String uri = subject.toString();
            String unique = predicate.toString();

        }
//        qe.close();

//        while (cursor.hasNext()) {
//            HashMap<String, Object> nextRow = cursor.next();
//            CrawleableUri newUri = null;
//            HashValue hashValue = null;
//            for (String key : nextRow.keySet()) {
//                if (key.equals(COLUMN_HASH_VALUE)) {
//                    String hashAsString = (String) nextRow.get(key);
//                    hashValue = hashValueForDecoding.decodeFromString(hashAsString);
//                } else if (key.equals(COLUMN_URI)) {
//                    try {
//                        newUri = new CrawleableUri(new URI((String) nextRow.get(key)));
//                    } catch (URISyntaxException e) {
//                        LOGGER.error("Error while constructing an uri: " + nextRow.get(key));
//                    }
//                }
//            }
//            newUri.addData(Constants.URI_HASH_KEY, hashValue);
//            urisToReturn.add(newUri);
//        }
//        cursor.close();
        return urisToReturn;
    }

    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {
        List<Triple> triplesFound = new ArrayList<>();
        for (CrawleableUri uri : uris) {
            HashValue tempHash = (HashValue) uri.getData(Constants.URI_HASH_KEY);
            String tempQuery = "INSERT DATA\n" +
                    " {\n" +
                    "   GRAPH <http://w3id.org/squirrel/metadata>\n" +
                    "     {\n" +
                    uri.getUri().toString() +" "+ COLUMN_PREDICATE + " "+ tempHash.encodeToString()+
                    "     }\n" +
                    " }";
            QueryExecution qe = connector.queryExecFactory.createQueryExecution(tempQuery);
            ResultSet rs = qe.execSelect();

        }
    }

}
