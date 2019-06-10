package org.dice_research.squirrel.data.uri.filter;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.modify.request.QuadDataAcc;
import org.apache.jena.sparql.modify.request.UpdateDataInsert;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.deduplication.hashing.HashValue;
import org.dice_research.squirrel.deduplication.hashing.UriHashCustodian;
import org.dice_research.squirrel.sink.SparqlBasedSinkDedup;
import org.dice_research.squirrel.sink.impl.sparql.QueryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class SPARQLKnownUriFilter implements KnownUriFilter, Closeable, UriHashCustodian {

    private static final Logger LOGGER = LoggerFactory.getLogger(SPARQLKnownUriFilter.class);

    public SparqlBasedSinkDedup connector = null;

    public SPARQLKnownUriFilter(String sparqlEndpointUrlQuery, String sparqlEndpointUrlUpdate, String username, String password) {
        this.connector = SparqlBasedSinkDedup.create(sparqlEndpointUrlQuery, sparqlEndpointUrlUpdate, username, password);
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
    public void open() {

    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        return false;
    }

    @Override
    public Set<CrawleableUri> getUrisWithSameHashValues(Set<HashValue> hashValuesForComparison) {
        Set<String> stringHashValues = new HashSet<>();
        Set<CrawleableUri> urisToReturn = new HashSet<>();

        //convert  hash values to set
        for (HashValue value : hashValuesForComparison) {
            stringHashValues.add(value.encodeToString());
        }

        for (String hashVal : stringHashValues){
            Query query = QueryGenerator.getInstance().getHashQuery(hashVal);
            QueryExecution qe = connector.queryExecFactory.createQueryExecution(query);
            ResultSet rs = qe.execSelect();

            while (rs.hasNext()) {
                QuerySolution sol = rs.nextSolution();
                RDFNode subject = sol.get("subject");
                String uri = subject.toString();
                LOGGER.info("Dedup_testing: uri getUrisSameHashValue: " + uri);
                CrawleableUri newUri = null;
                try{
                    newUri = new CrawleableUri(new URI(uri));
                }catch (URISyntaxException e) {
                        LOGGER.error("Error while constructing an uri: " + uri);
                }
                newUri.addData(Constants.URI_HASH_KEY, hashVal);
                urisToReturn.add(newUri);
            }
            qe.close();
        }
        return urisToReturn;
    }

    @Override
    public void addHashValuesForUris(List<CrawleableUri> uris) {

        Node graph = NodeFactory.createURI(QueryGenerator.METADATA_GRAPH_ID);
        for (CrawleableUri uri : uris) {
            try {
                HashValue tempHash = (HashValue) uri.getData(Constants.URI_HASH_KEY);
                Node subjectNode = NodeFactory.createURI(uri.getUri().toString());
                Node predicateNode = NodeFactory.createURI(QueryGenerator.COLUMN_PREDICATE_ID);
                Node objectNode = NodeFactory.createURI(tempHash.encodeToString());
                Triple triple = new Triple(subjectNode, predicateNode, objectNode);

                QuadDataAcc quads = new QuadDataAcc();
                quads.addQuad(new Quad(graph, triple));
                quads.setGraph(graph);
                UpdateDataInsert insert = new UpdateDataInsert(quads);
                UpdateProcessor processor = this.connector.updateExecFactory.createUpdateProcessor(new UpdateRequest(insert));
                processor.execute();
            }catch (Exception e) {
                LOGGER.error("Exception while sending update query.", e);
            }
        }
    }

}
