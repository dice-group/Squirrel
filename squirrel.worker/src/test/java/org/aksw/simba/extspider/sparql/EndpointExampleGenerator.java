package org.aksw.simba.extspider.sparql;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointExampleGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointExampleGenerator.class);

    public static final String W3C_SPARQL_ENDPOINT_LIST_URL = "http://www.w3.org/wiki/SparqlEndpoints";

    /**
     * Regular expression pattern to extract the URL from statements like
     * 
     * <pre>
     * &lt;td&gt; (2010-01-07) alive
     * &lt;/td&gt;
     * &lt;td&gt; &lt;a rel="nofollow" class="external text" href="http://example.org/sparql"&gt;endpoint&lt;/a&gt;
     * &lt;/td&gt;
     * </pre>
     * 
     */
    private static final String ENDPOINT_EXTRACTION_PATTERN = "(<td>[^<]*alive[^<]*</td>[^<]*<td>[^<]*<a[^>]*href[^=]*=[^\"]*\")([^\"]*)(\"[^>]*>[^<a-zA-z]*endpoint[^<a-zA-z]*</a>)";
    private static final int URL_CONTAINING_GROUP = 2;

    public static Set<String> getEndpointExamples() {
        Set<String> endpoints = new HashSet<String>();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(W3C_SPARQL_ENDPOINT_LIST_URL);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOGGER.error(
                        "Response has a bad status (" + response.getStatusLine().toString() + "). Returning null.");
                return null;
            }
            entity = response.getEntity();
            String content = IOUtils.toString(entity.getContent());
            Matcher matcher = Pattern.compile(ENDPOINT_EXTRACTION_PATTERN).matcher(content);
            while (matcher.find()) {
                endpoints.add(matcher.group(URL_CONTAINING_GROUP));
            }
        } catch (IOException e) {
            LOGGER.error("Got an exception while requesting a list of endpoints. Returning null.", e);
            return null;
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                }
            }
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(client);
        }
        return endpoints;
    }

    public static void main(String[] args) {
        Set<String> endpoints = getEndpointExamples();
        for (String endpoint : endpoints) {
            System.out.println(endpoint);
        }
    }
}
