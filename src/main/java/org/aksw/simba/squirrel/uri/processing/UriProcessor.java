package org.aksw.simba.squirrel.uri.processing;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.UriUtils;

/**
 * Uri Processor implementation.
 * 
 * @author Ivan Ermilov (iermilov@informatik.uni-leipzig.de)
 *
 */
public class UriProcessor implements UriProcessorInterface {
	
    public CrawleableUri recognizeUriType(CrawleableUri uri) {
    	URI uriString = uri.getUri();
    	String uriPath = uriString.getPath();
    	String[] uriParts = uriPath.split("/");
    	String uriFilePart = uriParts[uriParts.length - 1];
    	
    	String[] rdfDumpRegexs = {".*\\.rdf.*", 
    			                  ".*\\.ttl.*", 
    			                  ".*\\.nt.*", 
    			                  ".*\\.n3.*", 
    			                  ".*\\.zip.*", 
    			                  ".*\\.tar.*"
    			                 };
    	String[] sparqlRegexs = {".*sparql.*"};
    	String[] dereferenceableRegexs = {".*htm.*", ".*page.*", ".*resource.*"};
    	if(this.isStringMatchRegexs(uriPath, rdfDumpRegexs)) {
    		uri.setType(UriType.DUMP);
    	} else if(this.isStringMatchRegexs(uriPath, sparqlRegexs)) {
    		uri.setType(UriType.SPARQL);
    	} else if(this.isStringMatchRegexs(uriPath, dereferenceableRegexs)) {
    		uri.setType(UriType.DEREFERENCEABLE);
    	} else {
    		uri.setType(UriType.UNKNOWN);
    	}
    	
    	return uri;
    }
    
    private boolean isStringMatchRegexs(String string, String[] regexs) {
		return UriUtils.isStringMatchRegexs(string, regexs);
	}

    public CrawleableUri recognizeInetAddress(CrawleableUri uri) throws UnknownHostException {
    	String host = uri.getUri().getHost();
    	InetAddress ipAddress = InetAddress.getByName(host);
    	uri.setIpAddress(ipAddress);
    	return uri;
    }

}
