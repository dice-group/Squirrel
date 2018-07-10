package org.aksw.simba.squirrel.uri.processing;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * Uri Processor implementation.
 *
 * @author Ivan Ermilov (iermilov@informatik.uni-leipzig.de)
 *
 */
@Component
public class UriProcessor implements UriProcessorInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(UriProcessor.class);

    public CrawleableUri recognizeUriType(CrawleableUri uri) {
        LOGGER.debug("Recognizing type of {}", uri.toString());
        URI uriString = uri.getUri();
        String uriPath = uriString.getPath();
        LOGGER.debug("uriPath is {}", uriPath);

        String[] refDumpRegexps = {".*\\.rdf.*", ".*\\.ttl.*", ".*\\.nt.*", ".*\\.n3.*", ".*\\.zip.*", ".*\\.tar.*"};
        String[] snarlRegexps = {".*sparql.*"};
        String[] differentiableRegexps = {".*htm.*", ".*page.*", ".*resource.*"};

        try {
            if ((uriPath != null) && (this.isStringMatchRegexps(uriPath, refDumpRegexps))) {
                LOGGER.debug("uriPath is DUMP");
                uri.setType(UriType.DUMP);
            } else if ((uriPath != null) && (this.isStringMatchRegexps(uriPath, snarlRegexps))) {
                LOGGER.debug("uriPath is SPARQL");
                uri.setType(UriType.SPARQL);
            } else if ((uriPath != null) && (this.isStringMatchRegexps(uriPath, differentiableRegexps))) {
                LOGGER.debug("uriPath is DEREFERENCEABLE");
                uri.setType(UriType.DEREFERENCEABLE);
            } else {
                LOGGER.debug("uriPath is DEREFERENCEABLE");
                uri.setType(UriType.DEREFERENCEABLE);
            }
        } catch (Exception e) {
            LOGGER.debug("Uri {} could not be parsed. Skipping...", uri);
            e.printStackTrace();
        }
        LOGGER.debug("uri now is {}", uri.toString());
        return uri;
    }

    private boolean isStringMatchRegexps(String string, String[] regexs) {
        return UriUtils.isStringMatchRegexps(string, regexs);
    }

    public CrawleableUri recognizeInetAddress(CrawleableUri uri) throws UnknownHostException {
        String host;
        InetAddress ipAddress;
        if (!(uri.getUri() == null)) {
            host = uri.getUri().getHost();
            ipAddress = InetAddress.getByName(host);
            uri.setIpAddress(ipAddress);
            return uri;
        } else {
            URI newUri;
            newUri = URI.create("");
            ipAddress = InetAddress.getLocalHost();
            return new CrawleableUri(newUri, ipAddress, UriType.UNKNOWN);
        }
    }

}
