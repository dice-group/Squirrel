package org.aksw.simba.squirrel.collect;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.utils.TempFileHelper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SqlBasedUriCollectorTest {

    @Ignore
    @Test
    public void test() throws Exception {
        String dbdir = TempFileHelper.getTempDir("dbTest", "").getAbsolutePath() + File.separator + "test";

        Model model = ModelFactory.createDefaultModel();
        Set<String> expectedUris = new HashSet<String>();
        for (int i = 0; i < 1000; ++i) {
            Resource r = model.getResource("http://example.org/entity" + i);
            model.add(r, RDF.type, model.getResource("http://example.org/type" + (i & 1)));
            expectedUris.add(r.getURI());
        }
        expectedUris.add(RDF.type.getURI());
        expectedUris.add("http://example.org/type0");
        expectedUris.add("http://example.org/type1");

        CrawleableUri uri = new CrawleableUri(new URI("http://example.org/test"));

        GzipJavaUriSerializer serializer = new GzipJavaUriSerializer();

        SqlBasedUriCollector collector = SqlBasedUriCollector.create(serializer, dbdir);
        Assert.assertNotNull(collector);

        collector.openSinkForUri(uri);
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            collector.addTriple(uri, iterator.next().asTriple());
        }

        String uris[] = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(collector.getUris(uri), Spliterator.IMMUTABLE), false)
                .map((bytes) -> serializer.deserializeSafely(bytes)).toArray(String[]::new);
        collector.closeSinkForUri(uri);
        System.out.println(uris.length);
        Arrays.sort(uris);
        String eUris[] = expectedUris.toArray(new String[expectedUris.size()]);
        Arrays.sort(eUris);
        Assert.assertArrayEquals(eUris, uris);

        // Repeat the test with different URIs to make sure that the clear
        // method is working as well
        model = ModelFactory.createDefaultModel();
        expectedUris = new HashSet<String>();
        for (int i = 0; i < 1000; ++i) {
            Resource r = model.getResource("http://example2.org/entity" + i);
            model.add(r, RDF.type, model.getResource("http://example2.org/type" + (i & 1)));
            expectedUris.add(r.getURI());
        }
        expectedUris.add(RDF.type.getURI());
        expectedUris.add("http://example2.org/type0");
        expectedUris.add("http://example2.org/type1");
        uri = new CrawleableUri(new URI("http://example2.org/test"));

        collector.openSinkForUri(uri);
        iterator = model.listStatements();
        while (iterator.hasNext()) {
            collector.addTriple(uri, iterator.next().asTriple());
        }

        uris = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(collector.getUris(uri), Spliterator.IMMUTABLE), false)
                .map((bytes) -> serializer.deserializeSafely(bytes)).toArray(String[]::new);
        collector.closeSinkForUri(uri);
        Arrays.sort(uris);
        eUris = expectedUris.toArray(new String[expectedUris.size()]);
        Arrays.sort(eUris);
        Assert.assertArrayEquals(eUris, uris);
    }

}
