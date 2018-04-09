package org.aksw.simba.squirrel.data.uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Created by ivan on 29.02.16.
 */
public class UriUtils
{
    public static List<CrawleableUri> createCrawleableUriList(String[] seedUris)
    {
        return createCrawleableUriList(Arrays.asList(seedUris));
    }

    /**
     * Max file name length for linux OS is 256, some URIs are very long and will exceed that limit
     * leading to the file not being created. To avoid this error the file name should have a limit of 250 characters
     * to leave some space for the file extensions
     */
    public static final int MAX_FILE_NAME_LENGTH = 250;

    @Deprecated
    public static List<CrawleableUri> createCrawleableUriList(@SuppressWarnings("rawtypes") ArrayList uris, UriType type)
    {
        CrawleableUriFactoryImpl crawleableUriFactoryImpl = new CrawleableUriFactoryImpl();
        List<CrawleableUri> resultUris = getCrawleableUriList();

        for (Object uriString : uris)
        {
            URI uri = URI.create(uriString.toString());
            resultUris.add(crawleableUriFactoryImpl.create(uri, type));
        }

        return resultUris;
    }

    public static List<CrawleableUri> createCrawleableUriList(Collection<String> seedUris)
    {
        java.util.List<CrawleableUri> seed = getCrawleableUriList();
        CrawleableUriFactoryImpl crawleableUriFactoryImpl = new CrawleableUriFactoryImpl();
        CrawleableUri uri;
        for (String seedUri : seedUris)
        {
            uri = crawleableUriFactoryImpl.create(seedUri);
            if (uri != null)
            {
                seed.add(uri);
            }
        }

        return seed;
    }

    public static List<CrawleableUri> getCrawleableUriList()
    {
        return new ArrayList<CrawleableUri>();
    }

    public static String generateFileName(String uri, boolean useCompression)
    {
        StringBuilder builder = new StringBuilder(uri.length() + 10);
        char chars[] = uri.toCharArray();

        int length = chars.length < MAX_FILE_NAME_LENGTH ? chars.length : MAX_FILE_NAME_LENGTH;
        for (int i = 0; i < length; ++i)
        {
            if (Character.isLetterOrDigit(chars[i]))
            {
                builder.append(chars[i]);
            }
            else
            {
                if (i != length - 1)
                {
                    builder.append('_');
                }
            }
        }

        if (useCompression)
        {
            builder.append(".gz");
        }
        return builder.toString();
    }

    public static boolean isStringMatchRegexs(String string, String[] regexs)
    {
        for (String regex : regexs)
        {
            if (string.matches(regex))
            {
                return true;
            }
        }
        return false;
    }
}
