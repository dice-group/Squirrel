package org.aksw.simba.squirrel.seed.generator.impl;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 25.02.16.
 */
public class CkanSeedGeneratorImpl extends AbstractSeedGenerator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractSeedGenerator.class);

    static final String CkanApiEndpoint = "http://datahub.io/api/3";
    static final String RdfSearchAction = "/action/package_search?q=rdf";
    static final String RdfSearchQuery = CkanApiEndpoint + RdfSearchAction;
    static final String RdfCountQuery = RdfSearchQuery + "&rows=1";

    public CkanSeedGeneratorImpl(Frontier frontier) {
        super(frontier);
    }

    @Override
    public List<CrawleableUri> getSeed() {
        String[] seedUris = this.getRDFResources();
        return this.createCrawleableUriList(seedUris);
    }

    private String[] getRDFResources() {
        Integer queryCount = this.getQueryCount(RdfCountQuery);
        Integer pageSize = 50;
        Integer offset;
        String JSONString;
        ArrayList<String> output = new ArrayList<String>();;

        for(int i = 0; i < queryCount; i+=pageSize) {
            offset = pageSize + i;
            LOGGER.debug("Fetching {} resources with offset of {}", pageSize, offset);
            JSONString = this.getRDFDatasetListPage(pageSize, offset);
            JSONObject datasetsJson = new JSONObject(JSONString);
            if (datasetsJson.has("result")) {
                JSONObject result = datasetsJson.getJSONObject("result");
                JSONArray results = result.getJSONArray("results");
                for (int j = 0; j < results.length(); j++) {
                    JSONObject dataset = results.getJSONObject(j);
                    JSONArray resources = dataset.getJSONArray("resources");
                    for (int k = 0; k < resources.length(); k++) {
                        JSONObject resource = resources.getJSONObject(k);
                        String url = resource.getString("url");
                        output.add(url);
                    }
                }
            }

        }

        String[] outArray = new String[output.size()];
        outArray = output.toArray(outArray);
        return outArray;
    }

    private Integer extractCountFromQuery(String JSONString) {
        JSONObject datasetsJson = new JSONObject(JSONString);
        Integer count = 0;
        if (datasetsJson.has("result")) {
            JSONObject result = datasetsJson.getJSONObject("result");

            count = result.getInt("count");
        }
        return count;
    }

    private Integer getQueryCount(String Query) {
        String countResponse = this.HTTPGet(RdfCountQuery);
        return this.extractCountFromQuery(countResponse);
    }

    private String getRDFDatasetListPage(Integer pageSize, Integer offset) {
        return this.HTTPGet(CkanApiEndpoint + RdfSearchAction + "&rows=" + pageSize + "&start=" + offset);
    }

    private String HTTPGet(String HTTPQuery) {
        String responseBody = "";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(HTTPQuery);
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
            responseBody = httpclient.execute(httpget, responseHandler);
            httpclient.close();
        } catch (ClientProtocolException ServerFail){
            LOGGER.error("Datahub.io failed to process request: " + ServerFail.getMessage());
        } catch (IOException ClientProtocolException) {
            LOGGER.error("Client could not process request: " + ClientProtocolException.getMessage());
        }

        return responseBody;
    }

}
