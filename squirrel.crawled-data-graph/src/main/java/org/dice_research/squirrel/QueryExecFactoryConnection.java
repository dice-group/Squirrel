package org.dice_research.squirrel;

import com.sun.jndi.toolkit.url.Uri;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.LDAPCertStoreParameters;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class QueryExecFactoryConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecFactoryConnection.class);
    /**
     * The Query factory used to query the SPARQL endpoint.
     */
    protected static QueryExecutionFactory queryExecFactory = null;

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

    public static List<String> getSubDomain(String host) {
        Preconditions.checkNotNull(host);
        Joiner joiner = Joiner.on(".");
        List<String> domainParts = Lists.newLinkedList(Arrays.asList(host.split("\\.")));
        List<String> subdomain = Lists.newLinkedList();
        while (!domainParts.isEmpty()) {
            subdomain.add(joiner.join(domainParts));
            domainParts.remove(0);
        }
        LOGGER.info(String.valueOf(subdomain));
        return subdomain;
    }

    public static void main(String args[]) throws URISyntaxException {
        QueryExecFactoryConnection.create("http://localhost:8890/sparql-auth/", "dba", "pw123");
        Query domainQuery = SparqlQueryGenerator.getDomain();
        Query query = QueryFactory.create(domainQuery);
        QueryExecution qe = queryExecFactory.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            RDFNode uri = sol.get("uri");
            URI url = new URI(uri.toString());

            String hostname = url.getHost();
            String baseDomain = InternetDomainName.from(hostname).topPrivateDomain().toString();
            String temp1 = hostname.replace("." + baseDomain, "");
            getSubDomain(hostname);
            LOGGER.info(temp1);

            LOGGER.info(baseDomain);
            hostname = String.valueOf(InternetDomainName.from(hostname).topPrivateDomain());
            InternetDomainName it = InternetDomainName.from(hostname);
            String domainname = String.valueOf(it.publicSuffix());
            LOGGER.info(domainname);
            String temp = hostname.replaceAll("." + domainname, "");
            LOGGER.info(temp);

        }
        qe.close();
    }


}
