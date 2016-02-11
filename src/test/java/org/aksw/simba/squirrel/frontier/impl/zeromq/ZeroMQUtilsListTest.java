package org.aksw.simba.squirrel.frontier.impl.zeromq;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.zeromq.utils.ZeroMQUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ZeroMQUtilsListTest {

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(
                new Object[][] { { new CrawleableUri[] { new CrawleableUri(new URI("http://localhost/test")) } },
                        { new CrawleableUri[] { new CrawleableUri(new URI("http://localhost/test")),
                                new CrawleableUri(new URI("http://localhost/test1")) } } });
    }

    private CrawleableUri[] uris;

    public ZeroMQUtilsListTest(CrawleableUri[] uris) {
        this.uris = uris;
    }

    @Test
    public void test() {
        ByteBuffer buffer = ZeroMQUtils.generateUriListArray(Arrays.asList(uris));
        buffer.position(0);
        List<CrawleableUri> parsedUris = ZeroMQUtils.parseUris(buffer);
        Assert.assertNotNull(parsedUris);
        Set<CrawleableUri> uriSet = new HashSet<CrawleableUri>(parsedUris);
        for (int i = 0; i < uris.length; ++i) {
            Assert.assertTrue(uriSet.contains(uris[i]));
        }
        Assert.assertEquals(uris.length, parsedUris.size());
    }

}
