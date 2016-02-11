package org.aksw.simba.squirrel.data.uri;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CrawleableUriSerializationTest {

    @Parameters
    public static Collection<Object[]> data() throws Exception {
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
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("255.255.255.255")) } });
    }

    private CrawleableUri uri;

    public CrawleableUriSerializationTest(CrawleableUri uri) {
        this.uri = uri;
    }

    @Test
    public void test() throws URISyntaxException, UnknownHostException {
        CrawleableUri parsedUri;
        byte bytes[];

        uri = new CrawleableUri(new URI("http://google.de"));
        bytes = uri.toByteArray();
        parsedUri = CrawleableUri.fromByteArray(bytes);
        Assert.assertEquals(uri.getIpAddress(), parsedUri.getIpAddress());
        Assert.assertEquals(uri.getType(), parsedUri.getType());
        Assert.assertEquals(uri.getUri(), parsedUri.getUri());
    }
}
