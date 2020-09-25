package org.dice_research.squirrel.fetcher.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.tika.io.IOUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.fetcher.delay.Delayer;
import org.dice_research.squirrel.metadata.ActivityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Fetcher} which uses an HTTP client to fetch data and store it in a
 * temporary directory.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */

public class HTTPFetcher implements Fetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPFetcher.class);

    /**
     * The default HTTP Accept header value which simply accepts everything.
     */
    public static final String DEFAULT_ACCEPT_HEADER_STRING = "*/*";
    /**
     * The prefix which is added to HTTP response headers before they are stored the
     * {@link CrawleableUri}'s data map.
     */
    public static final String HTTP_RESPONSE_HEADER_PREFIX = "http-response-";
    /**
     * URI schemes which are accepted by this fetcher (i.e., {@code "http"} and
     * {@code "https"}).
     */
    protected static final Set<String> ACCEPTED_SCHEMES = new HashSet<String>(Arrays.asList("http", "https"));

    /**
     * The value which will be used for the HTTP Accept header if the give
     * {@link CrawleableUri} object does not define a header value.
     */
    protected String acceptHeader = DEFAULT_ACCEPT_HEADER_STRING;
    /**
     * The value which will be used for the HTTP Accept Charset header if the give
     * {@link CrawleableUri} object does not define a header value.
     */
    protected String acceptCharset = StandardCharsets.UTF_8.name();
    /**
     * The HTTP client instance used by this feature.
     */
    protected CloseableHttpClient client;
    /**
     * The temporary directory which will be used to store downloaded data.
     */
    protected File dataDirectory = FileUtils.getTempDirectory();

    public HTTPFetcher() {
        this(Constants.DEFAULT_USER_AGENT);
    }

    public HTTPFetcher(String userAgent) {
        this(HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager())
                .setUserAgent(userAgent).build());
    }

    public HTTPFetcher(CloseableHttpClient client) {
        this.client = client;
    }

    @Override
    public File fetch(CrawleableUri uri, Delayer delayer) {
        // Check whether this fetcher can handle the given URI
        if ((uri == null) || (uri.getUri() == null) || (!ACCEPTED_SCHEMES.contains(uri.getUri().getScheme()))) {
            return null;
        }
        // create temporary file
        File dataFile = null;
        try {
            dataFile = File.createTempFile("fetched_", "", dataDirectory);
        } catch (IOException e) {
            LOGGER.error("Couldn't create temporary file for storing fetched data. Returning null.", e);
            return null;
        }
        try {
            delayer.getRequestPermission();
            dataFile = requestData(uri, dataFile);
        } catch (ClientProtocolException e) {
            LOGGER.debug("HTTP Exception while requesting uri \"{}\". Returning null. Exception: {}", uri,
                    e.getMessage());
            ActivityUtil.addStep(uri, getClass(), e.getMessage());
            return null;
        } catch (FileNotFoundException e) {
            LOGGER.error("Couldn't create temporary file for storing fetched data. Returning null.", e);
            ActivityUtil.addStep(uri, getClass(), e.getMessage());
            return null;
        } catch (IOException e) {
            LOGGER.error("Couldn't fetched data. Returning null.", e);
            ActivityUtil.addStep(uri, getClass(), e.getMessage());
            return null;
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for request permission. Returning null.");
            ActivityUtil.addStep(uri, getClass(), e.getMessage());
            return null;
        } finally {
            // Inform the delayer that the request is done
            delayer.requestFinished();
        }
        ActivityUtil.addStep(uri, getClass());
        return dataFile;
    }

    protected File requestData(CrawleableUri uri, File outputFile)
            throws ClientProtocolException, FileNotFoundException, IOException {
        HttpGet request = null;
        request = new HttpGet(uri.getUri());
        request.addHeader(HttpHeaders.ACCEPT,
                MapUtils.getString(uri.getData(), Constants.URI_HTTP_ACCEPT_HEADER, acceptHeader));
        request.addHeader(HttpHeaders.ACCEPT_CHARSET,
                MapUtils.getString(uri.getData(), Constants.URI_HTTP_ACCEPT_HEADER, acceptCharset));

        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        OutputStream os = null;
        try {
            response = client.execute(request);

            // Handle response headers (especially the status and the content type)
            for (Header header : response.getAllHeaders()) {
                uri.addData(HTTP_RESPONSE_HEADER_PREFIX + header.getName(), header.getValue());
            }
            StatusLine status = response.getStatusLine();
            uri.addData(Constants.URI_HTTP_STATUS_CODE, status.getStatusCode());
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                LOGGER.info("Response of \"{}\" has the wrong status ({}). Returning null.", uri, status.toString());
                return null;
            }
            Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
            if (contentTypeHeader != null) {
                String typeValues[] = contentTypeHeader.getValue().split(";");
                uri.addData(Constants.URI_HTTP_MIME_TYPE_KEY, typeValues[0]);
                // If the content type contains a charset
                if (typeValues.length > 1) {
                    uri.addData(Constants.URI_HTTP_CHARSET_KEY, typeValues[1]);
                }
            } else {
                LOGGER.info("The response did not contain a content type header.");
            }

            // store response data
            entity = response.getEntity();
            InputStream is = entity.getContent();
            os = new BufferedOutputStream(new FileOutputStream(outputFile));
            StreamUtils.copy(is, os);
        } finally {
            IOUtils.closeQuietly(os);
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
        uri.addData(Constants.URI_DATA_FILE_NAME, outputFile.getAbsolutePath());
        return outputFile;
    }

    /**
     * The value of the HTTP Accept header field that is used if the given
     * {@link CrawleableUri} instance does not define this. <b>Note</b> that the
     * given string has to follow
     * <a href="https://tools.ietf.org/html/rfc7231#page-38">section 5.3.2 of
     * RFC-7231</a>.
     * 
     * @param acceptHeader the new value of the accept header as defined in
     *                     RFC-7231.
     */
    public void setAcceptHeader(String acceptHeader) {
        this.acceptHeader = acceptHeader;
    }

    public void setAcceptCharset(String acceptCharset) {
        this.acceptCharset = acceptCharset;
    }

    public void setDataDirectory(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
