package org.aksw.simba.squirrel.fetcher.deref;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.Fetcher;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DereferencingFetcher implements Fetcher, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DereferencingFetcher.class);

    private static final String REQUEST_ACCEPT_HEADER_VALUE = RDFLanguages.RDFXML.getContentType().getContentType();
    private static final String USER_AGENT = "Squirrel";

    protected CloseableHttpClient client;

    public DereferencingFetcher() {
        this.client = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager())
                .setUserAgent(USER_AGENT).build();
    }

    @Override
    public int fetch(CrawleableUri uri, Sink sink) {
        if (uri == null) {
            return 0;
        }
        Model model = null;
        try {
            model = requestModel(uri.getUri());
        } catch (org.apache.jena.atlas.web.HttpException e) {
            LOGGER.debug("HTTP Exception while requesting uri \"{}\". Returning null. Exception: {}", uri,
                    e.getMessage());
            return 0;
        } catch (org.apache.jena.riot.RiotException e) {
            LOGGER.debug("Riot Exception while parsing requested model of uri \"{}\". Returning null. Exception: {}",
                    uri, e.getMessage());
            return 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exception while requesting uri \"" + uri + "\". Returning null.", e);
            }
            return 0;
        }
        if (model == null) {
            return 0;
        }
        StmtIterator iterator = model.listStatements();
        Statement statement;
        int count = 0;
        while (iterator.hasNext()) {
            statement = iterator.next();
            sink.addTriple(uri, statement.asTriple());
            ++count;
        }
        return count;
    }

    protected Model requestModel(URI uri) {
        HttpGet request = null;
        try {
            request = new HttpGet(uri);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Exception while sending request for \"" + uri + "\". Returning null.", e);
            return null;
        }
        request.addHeader(HttpHeaders.ACCEPT, REQUEST_ACCEPT_HEADER_VALUE);
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        Model model = null;
        try {

            try {
                response = client.execute(request);
            } catch (java.net.SocketException e) {
                LOGGER.error("Exception while sending request to \"" + uri + "\". Returning null.", e);
                return null;
            } catch (UnknownHostException e) {
                LOGGER.info("Couldn't find host of \"" + uri + "\". Returning null.");
                return null;
            } catch (Exception e) {
                LOGGER.error("Exception while sending request to \"" + uri + "\". Returning null.", e);
                return null;
            }
            StatusLine status = response.getStatusLine();
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                LOGGER.warn("Response of \"{}\" has the wrong status ({}). Returning null.", uri, status.toString());
                return null;
            }
            // receive NIF document
            entity = response.getEntity();
            Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
            if (contentTypeHeader == null) {
                LOGGER.error("The response did not contain a content type header. Returning null.");
                return null;
            }
            ContentType contentType = ContentType.create(contentTypeHeader.getValue());
            Lang language = RDFLanguages.contentTypeToLang(contentType);
            if (language == null) {
                LOGGER.error("Couldn't find an RDF language for the content type header value \"{}\". Returning null.",
                        contentTypeHeader.getValue());
                return null;
            }
            // read response and parse NIF
            try {
                model = ModelFactory.createDefaultModel();
                RDFDataMgr.read(model, entity.getContent(), language);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response for the URI \"" + uri + "\". Returning null", e);
            }
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e1) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return model;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

}
