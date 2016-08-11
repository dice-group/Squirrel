package org.aksw.simba.squirrel.uri.processing;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uri Processor implementation.
 * 
 * @author Ivan Ermilov (iermilov@informatik.uni-leipzig.de)
 *
 */
public class UriProcessor implements UriProcessorInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(UriProcessor.class);
	
    public CrawleableUri recognizeUriType(CrawleableUri uri) {
    	URI uriString = uri.getUri();
    	String uriPath = uriString.getPath();
    	
    	String[] rdfDumpRegexs = {".*\\.rdf.*", 
    			                  ".*\\.ttl.*", 
    			                  ".*\\.nt.*", 
    			                  ".*\\.n3.*", 
    			                  ".*\\.zip.*", 
    			                  ".*\\.tar.*"
    			                 };
    	String[] sparqlRegexs = {".*sparql.*"};
    	String[] dereferenceableRegexs = {".*htm.*", ".*page.*", ".*resource.*"};

		try {
			if (this.isStringMatchRegexs(uriPath, rdfDumpRegexs)) {
				uri.setType(UriType.DUMP);
			} else if (this.isStringMatchRegexs(uriPath, sparqlRegexs)) {
				uri.setType(UriType.SPARQL);
			} else if (this.isStringMatchRegexs(uriPath, dereferenceableRegexs)) {
				uri.setType(UriType.DEREFERENCEABLE);
			} else {
				uri.setType(UriType.DEREFERENCEABLE);
			}
		} catch(Exception e) {
			LOGGER.debug("Uri {} could not be parsed. Skipping...", uri);
			e.printStackTrace();
		}
    	
    	return uri;
    }
    
    private boolean isStringMatchRegexs(String string, String[] regexs) {
		return UriUtils.isStringMatchRegexs(string, regexs);
	}

    public CrawleableUri recognizeInetAddress(CrawleableUri uri) throws UnknownHostException {
    	String host;
		InetAddress ipAddress;
		if(! (uri.getUri() == null)) {
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
