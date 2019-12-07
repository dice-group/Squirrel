package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class UriVariantImplTest {


    UriVariants variantUriObject = new UriVariantImpl();

    @Test
    public void getUriVariants(){
        URI uri = null;
        try {
            uri = new URI("http://www.example.com/a/./b/../c");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        CrawleableUri uriObject = new CrawleableUri(uri);
        List<CrawleableUri> expectedUriVariants = new ArrayList<>();
        try {
            URI expectedUri = new URI("www.example.com");
            CrawleableUri curi = new CrawleableUri(expectedUri);
            expectedUriVariants.add(curi);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        List<CrawleableUri> actualUriVariants = variantUriObject.getUriVariants(uriObject);
    }
}
