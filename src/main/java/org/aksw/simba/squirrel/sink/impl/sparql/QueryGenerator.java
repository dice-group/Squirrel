package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to provides templates for basic SPARQL commands needed in this project.
 */
public class QueryGenerator {

    private static final QueryGenerator instance = new QueryGenerator();
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryGenerator.class);


    private QueryGenerator() {
    }

    public static QueryGenerator getInstance() {
        return instance;
    }

    /**
     * Return an Add Query for the given graph id and triple.
     *
     * @param graphIdentifier the given graph id.
     * @param triple          The given triple.
     * @return The generated query.
     */
    public String getAddQuery(String graphIdentifier, Triple triple, boolean isMetaData) {
        String strQuery = "";
        if (isMetaData) {
            strQuery += "prefix prov: <http://www.w3.org/ns/prov-o/> ";
            strQuery += "prefix sq: <https://w3id.org/squirrel/> ";
        }
        strQuery += "INSERT DATA { GRAPH <" + graphIdentifier + "> { ";
        if (!isMetaData) {
            strQuery += "<" + triple.getSubject() + "> <" + triple.getPredicate() + "> <" + triple.getObject() + "> ; ";
        } else {
            strQuery += "<" + triple.getSubject().getName() + "> " + triple.getPredicate().getName() + " <" + triple.getObject().getName() + "> ; ";
        }
        strQuery += "} }";
        return strQuery;
    }

    /**
     * Return an Add Query for a given uri and a triple.
     * @param uri The given Uri.
     * @param triple The given triple.
     * @return The generated query.
     */
    public String getAddQuery(CrawleableUri uri, Triple triple, boolean isMetaData) {
        return getAddQuery(uri.getUri().toString(), triple, isMetaData);
    }

    /**
     * Return a select all query for a given uri.
     * @param uri The given uri.
     * @return The generated query.
     */
    public Query getSelectAllQuery(CrawleableUri uri) {
        return getSelectQuery(uri, null, true);
    }

    /**
     * Return a select query for a given uri and triple.
     * @param uri The given uri.
     * @param triple The given triple.
     * @return The generated query.
     */
    @SuppressWarnings("unused")
    public Query getSelectQuery(CrawleableUri uri, Triple triple) {
        return getSelectQuery(uri, triple, false);
    }

    /**
     * Return a select query for the given uri and triple.
     *
     * @param uri        The given uri.
     * @param triple     The given triple.
     * @param bSelectAll Indicates whether the query should be a select all query or not.
     * @return
     */
    public Query getSelectQuery(CrawleableUri uri, Triple triple, boolean bSelectAll) {
        String strQuery = "SELECT ?subject ?predicate ?object WHERE { GRAPH <" + uri.getUri().toString() + "> { ";
        if (bSelectAll) {
            strQuery += "?subject ?predicate ?object ";
        } else {
            strQuery += triple.getSubject().getName() + " " + triple.getPredicate().getName() + " " + triple.getObject().getName() + " ; ";
        }
        strQuery += "} }";
        Query query = QueryFactory.create(strQuery);
        return query;
    }


}
