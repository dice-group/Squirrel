package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class UriGeneratorImplTest {


    UriGenerator variantUriObject = new UriGeneratorImpl();

    @Test
    public void getUriVariants(){
        CrawleableUri originalUri = null;
        try {
            originalUri = new CrawleableUri( new URI("http://www.example.com/a/./b/../c?b=1&a=2&a=1"));
            } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        List<CrawleableUri> actualUriVariants = variantUriObject.getUriVariants(originalUri);
        boolean domainFlag = false, voidFlag = false;
        try {
            // check if domain variant is generated
            CrawleableUri domainUri = new CrawleableUri( new URI("www.example.com"));
            if(actualUriVariants.contains(domainUri))
                domainFlag = true;
            // check if void variant is generated
            CrawleableUri voidUri = new CrawleableUri( new URI("http://www.example.com/.well-known/void"));
            if(actualUriVariants.contains(voidUri))
                voidFlag = true;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(domainFlag);
        Assert.assertTrue(voidFlag);
    }
}
