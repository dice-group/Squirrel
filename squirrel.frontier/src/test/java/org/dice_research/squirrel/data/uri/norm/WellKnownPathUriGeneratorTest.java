package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class WellKnownPathUriGeneratorTest {
    private UriGenerator variantUriObject = new WellKnownPathUriGenerator();

    @Test
    public void getVariant(){
        CrawleableUri originalUri = null;
        try {
            originalUri = new CrawleableUri( new URI("http://www.example.com/a/./b/../c?b=1&a=2&a=1"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        boolean voidFlag = false;
        CrawleableUri actualUriVariant = variantUriObject.getUriVariant(originalUri);
        try {
            CrawleableUri voidUri = new CrawleableUri( new URI("http://www.example.com/.well-known/void"));
            if(actualUriVariant.equals(voidUri))
                voidFlag = true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(voidFlag);
    }
}
