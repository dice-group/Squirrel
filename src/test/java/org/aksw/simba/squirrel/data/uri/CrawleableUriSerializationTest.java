package org.aksw.simba.squirrel.data.uri;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import org.aksw.simba.squirrel.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.gson.Gson;

@RunWith(Parameterized.class)
public class CrawleableUriSerializationTest {

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        CrawleableUri temp = new CrawleableUri(new URI("http://localhost/test"));
        temp.addData(Constants.URI_TYPE_KEY, Constants.URI_TYPE_VALUE_DEREF);
        temp.addData(Constants.URI_HTTP_MIME_TYPE_KEY, "application/json-ld");
        temp.addData(Constants.URI_HTTP_CHARSET_KEY, "utf-8");
        temp.addData(Constants.URI_PREFERRED_RECRAWL_ON, System.currentTimeMillis() + 100000L);
        
        return Arrays.asList(new Object[][] { { new CrawleableUri(new URI("http://localhost/test")) },
                { new CrawleableUri(new URI("http://google.de")) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("192.168.100.1"),
                        UriType.DEREFERENCEABLE) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("192.168.100.1"),
                        UriType.DUMP) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("192.168.100.1"),
                        UriType.SPARQL) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("192.168.100.1"),
                        UriType.UNKNOWN) },
                { new CrawleableUri(new URI("http://dbpedia.org"), null, UriType.SPARQL) },
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("255.255.255.255")) } ,
                { temp } });
    }

    private CrawleableUri uri;

    public CrawleableUriSerializationTest(CrawleableUri uri) {
        this.uri = uri;
    }

    @Test
    public void test() throws URISyntaxException, UnknownHostException {
        CrawleableUri parsedUri;
        byte bytes[];

        bytes = uri.toByteArray();
        parsedUri = CrawleableUri.fromByteArray(bytes);
        Assert.assertEquals(uri.getIpAddress(), parsedUri.getIpAddress());
        Assert.assertEquals(uri.getType(), parsedUri.getType());
        Assert.assertEquals(uri.getUri(), parsedUri.getUri());
    }

    @Test
    public void gsonTest() throws URISyntaxException, UnknownHostException {
        CrawleableUri parsedUri;
        Gson gson = new Gson();
        
        String json = gson.toJson(uri);
        System.out.println(json);
        parsedUri = gson.fromJson(json, CrawleableUri.class);
        Assert.assertEquals(uri.getIpAddress(), parsedUri.getIpAddress());
        Assert.assertEquals(uri.getType(), parsedUri.getType());
        Assert.assertEquals(uri.getUri(), parsedUri.getUri());
        for(String key : uri.getData().keySet()) {
            Assert.assertEquals(uri.getData(key), parsedUri.getData(key));
        }
        Assert.assertEquals(uri.getData().size(), parsedUri.getData().size());
    }
}
