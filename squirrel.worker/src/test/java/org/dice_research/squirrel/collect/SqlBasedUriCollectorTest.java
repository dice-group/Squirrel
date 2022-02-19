package org.dice_research.squirrel.collect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.squirrel.collect.SqlBasedUriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.utils.TempFileHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SqlBasedUriCollectorTest {

    GzipJavaUriSerializer serializer;
    String dbdir;
    File file;
    FileReader fr;
    BufferedReader br;

    @Before
    public void prepare() throws Exception {
        serializer = new GzipJavaUriSerializer();
        dbdir = TempFileHelper.getTempDir("dbTest", "").getAbsolutePath() + File.separator + "test";
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource("collector/mCloudURIs.txt").getFile());
        fr = new FileReader(file);
        br = new BufferedReader(fr);

    }

    @Test
    public void test() throws Exception {

        CrawleableUri uri = new CrawleableUri(new URI("http://example.org/test1"));

        SqlBasedUriCollector collector = new SqlBasedUriCollector(serializer, "testUris");

        collector.openSinkForUri(uri);


        Model model = ModelFactory.createDefaultModel();
        Set<String> expectedUris = new TreeSet<String>();
        for (int i = 0; i < 100; ++i) {
            Resource r = model.getResource("http://example.org/entity" + i);
            model.add(r, RDF.type, model.getResource("http://example.org/type" + (i & 1)));
            expectedUris.add(r.getURI());
            collector.addNewUri(uri, new CrawleableUri(new URI(r.getURI())));
        }

        Iterator<byte[]> iterator = collector.getUris(uri);

        Set<String> listCuris = new TreeSet<String>();

        while(iterator.hasNext()) {
            listCuris.add( ((CrawleableUri) serializer.deserialize(iterator.next())).getUri().toString() );

        }

        collector.closeSinkForUri(uri);
        Assert.assertEquals(expectedUris, listCuris);


    }

    @Test
    public void testFile() throws Exception {
        String sCurrentLine;

        Set<URI> expectedUris = new TreeSet<URI>();
        Set<URI> listUris = new TreeSet<URI>();

        SqlBasedUriCollector collector = new SqlBasedUriCollector(serializer, "testUris");
        CrawleableUri uri = new CrawleableUri(new URI("http://example.org/test2"));
        collector.openSinkForUri(uri);

        while ((sCurrentLine = br.readLine()) != null) {
            expectedUris.add(new URI(sCurrentLine));
            collector.addNewUri(uri, new CrawleableUri(new URI(sCurrentLine)));
        }

        Iterator<byte[]> it = collector.getUris(uri);
        while(it.hasNext()) {
            listUris.add(new URI( ((CrawleableUri) serializer.deserialize(it.next())).getUri().toString() ));
        }


        collector.closeSinkForUri(uri);
        Assert.assertEquals(expectedUris.size(), listUris.size());



    }

}
