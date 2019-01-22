package org.dice_research.squirrel.data.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.dice_research.squirrel.Constants;

/**
 * Created by ivan on 29.02.16.
 */
public class UriUtils {
    public static List<CrawleableUri> createCrawleableUriList(String[] seedUris) {
        return createCrawleableUriList(Arrays.asList(seedUris));
    }

    @Deprecated
    public static List<CrawleableUri> createCrawleableUriList(@SuppressWarnings("rawtypes") ArrayList uris, UriType type) {
        CrawleableUriFactoryImpl crawleableUriFactoryImpl = new CrawleableUriFactoryImpl();
        List<CrawleableUri> resultUris = getCrawleableUriList();

        for (Object uriString : uris) {
            URI uri = URI.create(uriString.toString());
            resultUris.add(crawleableUriFactoryImpl.create(uri, type));
        }

        return resultUris;
    }

    public static List<CrawleableUri> createCrawleableUriList(Collection<String> seedUris) {
        java.util.List<CrawleableUri> seed = getCrawleableUriList();
        CrawleableUriFactoryImpl crawleableUriFactoryImpl = new CrawleableUriFactoryImpl();
        CrawleableUri uri;
        for (String seedUri : seedUris) {
            uri = crawleableUriFactoryImpl.create(seedUri);
            if (uri != null) {
                seed.add(uri);
            }
        }

        return seed;
    }

    public static List<CrawleableUri> getCrawleableUriList() {
        return new ArrayList<CrawleableUri>();
    }

    /**
     * 
     * @deprecated use {@link #generateFileName(String, String)} instead.
     */
    @Deprecated
    public static String generateFileName(String uri, boolean useCompression) {
        return generateFileName(uri, useCompression ? "gz" : null);
    }

    @Deprecated
    public static String generateFileName(String uri, String fileEnding) {
        StringBuilder builder = new StringBuilder(uri.length() + 10);
        char chars[] = uri.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (Character.isLetterOrDigit(chars[i])) {
                builder.append(chars[i]);
            } else {
                builder.append('_');
            }
        }
        if (fileEnding != null) {
            builder.append(".");
            builder.append(fileEnding);
        }
        return builder.toString();
    }
    
    public static String generateFileName(CrawleableUri curi, String fileEnding) {
    	Object temp = curi.getData(Constants.UUID_KEY);
    	
    	String fileName = "";
    	
    	if(temp == null) {
    		fileName = curi.getUri().toString();
    	}else {
    		fileName = temp.toString();
    	}
        StringBuilder builder = new StringBuilder(fileName.length() + 10);
        char chars[] = fileName.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (Character.isLetterOrDigit(chars[i])) {
                builder.append(chars[i]);
            } else {
                builder.append('_');
            }
        }
        if (fileEnding != null) {
            builder.append(".");
            builder.append(fileEnding);
        }
        return builder.toString();
    }

    public static boolean isStringMatchRegexps(String string, String[] regexs) {
        for (String regex : regexs) {
            if (string.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
