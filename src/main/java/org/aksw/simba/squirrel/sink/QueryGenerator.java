package org.aksw.simba.squirrel.sink;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

public class QueryGenerator {

    private static QueryGenerator instance = new QueryGenerator();

    private QueryGenerator() {
    }

    public static QueryGenerator getInstance() {
        return instance;
    }

    public String getAddQuery(CrawleableUri uri, Triple triple) {
        String strQuery = "INSERT DATA { GRAPH <" + uri.getUri().toString() + "> { ";
        //strQuery += "\""+triple.getSubject().getName() + "\" \"" + triple.getPredicate().getName() + "\" \"" + triple.getObject().getName() + "\" ; ";
        strQuery += "<" + triple.getSubject().getName() + "> <" + triple.getPredicate().getName() + "> <" + triple.getObject().getName() + "> ; ";
        strQuery += "} }";
        System.out.println(strQuery);
        return strQuery;

    }

    public Query getSelectAllQuery(CrawleableUri uri) {
        return generateSelectQuery(uri, null, true);
    }

    public Query getSelectQuery(CrawleableUri uri, Triple triple) {
        return generateSelectQuery(uri, triple, false);
    }

    public Query generateSelectQuery(CrawleableUri uri, Triple triple, boolean bSelecteAll) {
        String strQuery = "SELECT ?subject ?predicate ?object WHERE { GRAPH <" + uri.getUri().toString() + "> { ";
        if (bSelecteAll) {
            strQuery += "?subject ?predicate ?object ";
        } else {
            strQuery += triple.getSubject().getName() + " " + triple.getPredicate().getName() + " " + triple.getObject().getName() + " ; ";
        }
        strQuery += "} }";
        System.out.println(strQuery);
        Query query = QueryFactory.create(strQuery);
        System.out.println("Generated Query: " + query.toString());
        return query;
    }


}
