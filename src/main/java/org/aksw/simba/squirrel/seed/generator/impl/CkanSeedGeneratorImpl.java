package org.aksw.simba.squirrel.seed.generator.impl;

import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ivan on 25.02.16.
 */
public class CkanSeedGeneratorImpl extends AbstractSeedGenerator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractSeedGenerator.class);

    static final String CkanApiEndpoint = "http://datahub.io/api/3";
    static final String RdfSearchAction = "/action/package_search?q=rdf";

    public CkanSeedGeneratorImpl(Frontier frontier) {
        super(frontier);
    }

    @Override
    public List<CrawleableUri> getSeed() {
        String[] seedUris = this.getDumpsFromCkan();
        return this.createCrawleableUriList(seedUris);
    }

    private String[] getDumpsFromCkan() {
        String[] dumpUris = {};


        return dumpUris;
    }

    private void getDatasetList() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(CkanApiEndpoint + RdfSearchAction);
            LOGGER.debug("Executing request: " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);

            httpclient.close();
        } catch (ClientProtocolException ServerFail){
            LOGGER.error("Datahub.io failed to prcess request: " + ServerFail.getMessage());
        } catch (IOException ClientProtocolException) {
            LOGGER.error("Client could not process request: " + ClientProtocolException.getMessage());
        }
    }

}
