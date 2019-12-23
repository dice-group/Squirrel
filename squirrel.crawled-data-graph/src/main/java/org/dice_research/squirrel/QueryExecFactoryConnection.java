package org.dice_research.squirrel;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactoryHttp;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.ext.com.google.common.base.Joiner;
import org.apache.jena.ext.com.google.common.base.Preconditions;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.ext.com.google.common.net.InternetDomainName;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.DatasetDescription;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class QueryExecFactoryConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecFactoryConnection.class);
    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    protected static QueryExecutionFactory queryExecFactory = null;
    static HashMap<String, Object> parameters = new HashMap<String, Object>();


    static HashMap<String, Object> newParameters = new HashMap<String, Object>();

    public QueryExecFactoryConnection(QueryExecutionFactory queryExecFactory, UpdateExecutionFactory updateExecFactory) {
        this.queryExecFactory = queryExecFactory;
        LOGGER.info("Connected");
    }

    public QueryExecFactoryConnection(QueryExecutionFactory queryExecFactory) {
        this.queryExecFactory = queryExecFactory;
        LOGGER.info("crawled data Connected");
    }


    public static QueryExecFactoryConnection create(String sparqlEndpointUrl) {
        return create(sparqlEndpointUrl, null, null);
    }

    public static QueryExecFactoryConnection create(String sparqlEndpointUrl, String username, String password) {
        QueryExecutionFactory queryExecFactory = null;
        UpdateExecutionFactory updateExecFactory = null;
        if (username != null && password != null) {
            // Create the factory with the credentials
            final Credentials credentials = new UsernamePasswordCredentials(username, password);
            HttpAuthenticator authenticator = new HttpAuthenticator() {
                @Override
                public void invalidate() {
                }

                @Override
                public void apply(AbstractHttpClient client, HttpContext httpContext, URI target) {
                    client.setCredentialsProvider(new CredentialsProvider() {
                        @Override
                        public void clear() {
                        }

                        @Override
                        public Credentials getCredentials(AuthScope scope) {
                            return credentials;
                        }

                        @Override
                        public void setCredentials(AuthScope arg0, Credentials arg1) {
                            LOGGER.error("I am a read-only credential provider but got a call to set credentials.");
                        }
                    });
                }
            };
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl, new DatasetDescription(),
                authenticator);
            updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl, authenticator);
        } else {
            queryExecFactory = new QueryExecutionFactoryHttp(sparqlEndpointUrl);
            updateExecFactory = new UpdateExecutionFactoryHttp(sparqlEndpointUrl);
        }
        return new QueryExecFactoryConnection(queryExecFactory, updateExecFactory);
    }

    public static List<String> getDomainLevels(String host) {
        Preconditions.checkNotNull(host);
        Joiner joiner = Joiner.on(".");
        List<String> domainParts = Lists.newLinkedList(Arrays.asList(host.split("\\.")));
        List<String> domainLevels = Lists.newLinkedList();
        while (!domainParts.isEmpty()) {
            domainLevels.add(joiner.join(domainParts));
            domainParts.remove(0);
        }
        return domainLevels;
    }

    public static String getPath(String fullPath) {
        String[] path = fullPath.split("/");
        List<String> a = new ArrayList<String>();
        for (String s : path) {
            a.add(s);
        }
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            sb2.append(a.get(i) + "/");
        }
        String p1 = sb2.toString();
        return p1;

    }

    public static void main(String[] args) throws URISyntaxException {
        QueryExecFactoryConnection.create("http://localhost:8890/sparql-auth/", "dba", "pw123");
        Query domainQuery = SparqlQueryGenerator.getDomain();
        Query query = QueryFactory.create(domainQuery);
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        List<String> list = new ArrayList();
        String hostname, fullHostName, subdomain;
        URI url;
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode uri = sol.get("uri");
            url = new URI(uri.toString());
            hostname = url.getHost();
            fullHostName = hostname;

            String segments = url.getPath();
            String urlQuery = url.getQuery();
            String baseDomain = InternetDomainName.from(hostname).topPrivateDomain().toString();
            String fullPath = baseDomain + segments;
            subdomain = hostname.replace("." + baseDomain, "");
            getDomainLevels(hostname);
            getPath(fullPath);
            hostname = String.valueOf(InternetDomainName.from(hostname).topPrivateDomain());
            InternetDomainName it = InternetDomainName.from(hostname);
            String domainname = String.valueOf(it.publicSuffix());
            String domainName = hostname.replaceAll("." + domainname, "");
            //   LOGGER.info(String.valueOf(url));
            //     LOGGER.info("full hostname: " + fullHostName);
            //    LOGGER.info("Pay level domain: " + hostname);
            list.add(hostname);


        }
        qe.close();
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        Set<String> domains = new HashSet<String>(list);
        for (String word : domains) {
            String pld = word;
            int triples = Collections.frequency(list, word);

            Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("cgraph", "param"));
            try (Session session = driver.session()) {

                parameters.put("plDomain", pld);
                parameters.put("triples", triples);
                String cypherQuery =
                    "CREATE (m1:data1 { Node1:$plDomain}) -[r:triples]->(m2:data2 { Node2:$triples})" +
                        "RETURN m1,r,m2";

                StatementResult result = session.run(cypherQuery, parameters);
            }
            driver.close();
        }
    }
}

