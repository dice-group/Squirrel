package org.aksw.simba.squirrel.data.uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ivan on 29.02.16.
 */
public class UriUtils {
    public static List<CrawleableUri> createCrawleableUriList(String[] seedUris) {
        java.util.List<CrawleableUri> seed = getSeedList();
        CrawleableUriFactoryImpl crawleableUriFactoryImpl = new CrawleableUriFactoryImpl();
        for(int i=0; i<seedUris.length; i++) {
            seed.add(crawleableUriFactoryImpl.create(seedUris[i]));
        }

        return seed;
    }

    public static List<CrawleableUri> createCrawleableUriList(Set<String> seedUris) {
        java.util.List<CrawleableUri> seed = getSeedList();
        CrawleableUriFactoryImpl crawleableUriFactoryImpl = new CrawleableUriFactoryImpl();
        for(String seedUri : seedUris) {
            seed.add(crawleableUriFactoryImpl.create(seedUri));
        }

        return seed;
    }

    public static List<CrawleableUri> getSeedList() {
        java.util.List<CrawleableUri> seed;
        return new ArrayList<CrawleableUri>();
    }
}
