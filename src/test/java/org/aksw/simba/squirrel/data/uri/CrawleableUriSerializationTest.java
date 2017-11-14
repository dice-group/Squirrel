package org.aksw.simba.squirrel.data.uri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.aksw.simba.squirrel.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xerial.snappy.Snappy;

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
                { new CrawleableUri(new URI("http://google.de"), InetAddress.getByName("255.255.255.255")) },
                { temp } });
    }

    private CrawleableUri uri;

    public CrawleableUriSerializationTest(CrawleableUri uri) {
        this.uri = uri;
    }

    @Test
    public void testGSON() throws URISyntaxException, ClassNotFoundException, IOException {
        CrawleableUri parsedUri;
        Gson gson = new Gson();
        String json = gson.toJson(uri);
        parsedUri = gson.fromJson(json, CrawleableUri.class);
        Assert.assertEquals(uri.getIpAddress(), parsedUri.getIpAddress());
        Assert.assertEquals(uri.getType(), parsedUri.getType());
        Assert.assertEquals(uri.getUri(), parsedUri.getUri());
        for (String key : uri.getData().keySet()) {
            Assert.assertEquals(uri.getData(key), parsedUri.getData(key));
        }
        Assert.assertEquals(uri.getData().size(), parsedUri.getData().size());
    }

    @Test
    public void testJavaWithSnappy() throws URISyntaxException, ClassNotFoundException, IOException {
        CrawleableUri parsedUri;
        byte[] data = toString(uri);
        System.out.println("Snappy: data.length=" + data.length);
        parsedUri = (CrawleableUri) fromString(data);
        Assert.assertEquals(uri.getIpAddress(), parsedUri.getIpAddress());
        Assert.assertEquals(uri.getType(), parsedUri.getType());
        Assert.assertEquals(uri.getUri(), parsedUri.getUri());
        for (String key : uri.getData().keySet()) {
            Assert.assertEquals(uri.getData(key), parsedUri.getData(key));
        }
        Assert.assertEquals(uri.getData().size(), parsedUri.getData().size());
    }

    private static byte[] toString(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        String stringToSend = Base64.getEncoder().encodeToString(baos.toByteArray());
        byte[] bytesToSend = stringToSend.getBytes();
        return Snappy.compress(bytesToSend);
    }

    private static byte[] toGzippedString(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(new GZIPOutputStream(baos));
        os.writeObject(obj);
        os.close();
        return baos.toByteArray();
    }

    private static Object fromString(byte[] compressedString) throws IOException, ClassNotFoundException {
        byte[] uncompressedString = Snappy.uncompress(compressedString);
        String receivedString = new String(uncompressedString);
        byte[] data = Base64.getDecoder().decode(receivedString);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    private static Object fromGzippedString(byte[] compressedString) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(compressedString)));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    @Test
    public void testJavaWithGzip() throws URISyntaxException, ClassNotFoundException, IOException {
        CrawleableUri parsedUri;
        byte[] data = toGzippedString(uri);
        System.out.println("Gzip:   data.length=" + data.length);
        parsedUri = (CrawleableUri) fromGzippedString(data);
        Assert.assertEquals(uri.getIpAddress(), parsedUri.getIpAddress());
        Assert.assertEquals(uri.getType(), parsedUri.getType());
        Assert.assertEquals(uri.getUri(), parsedUri.getUri());
        for (String key : uri.getData().keySet()) {
            Assert.assertEquals(uri.getData(key), parsedUri.getData(key));
        }
        Assert.assertEquals(uri.getData().size(), parsedUri.getData().size());
    }
}
