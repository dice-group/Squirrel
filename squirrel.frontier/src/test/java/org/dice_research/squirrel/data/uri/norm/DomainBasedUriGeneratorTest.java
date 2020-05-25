package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class DomainBasedUriGeneratorTest {


    UriGenerator variantUriObject = new DomainBasedUriGenerator();

    @Test
    public void getUriVariant(){
        CrawleableUri originalUri = null;
        try {
            originalUri = new CrawleableUri( new URI("http://www.example.com/a/./b/../c?b=1&a=2&a=1"));
            } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        CrawleableUri actualUriVariant = variantUriObject.getUriVariant(originalUri);
        boolean domainFlag = false;
        try {
            // check if domain variant is generated
            CrawleableUri domainUri = new CrawleableUri( new URI("www.example.com"));
            if(actualUriVariant.equals(domainUri))
                domainFlag = true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(domainFlag);
    }
}
