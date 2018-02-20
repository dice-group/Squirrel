package org.aksw.simba.squirrel.collect;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.utils.TempFileHelper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

public class SqlBasedUriCollectorTest {

    @Test
    public void test() throws Exception {
        String dbdir = TempFileHelper.getTempDir("dbTest", "").getAbsolutePath() + File.separator + "test";
        
        CrawleableUri uri = new CrawleableUri(new URI("http://example.org/test"));
        GzipJavaUriSerializer serializer = new GzipJavaUriSerializer();

        SqlBasedUriCollector collector = SqlBasedUriCollector.create(serializer, dbdir);
        
        collector.openSinkForUri(uri);


        Model model = ModelFactory.createDefaultModel();
        Set<String> expectedUris = new HashSet<String>();
        for (int i = 0; i < 100; ++i) {
            Resource r = model.getResource("http://example.org/entity" + i);
            model.add(r, RDF.type, model.getResource("http://example.org/type" + (i & 1)));
            expectedUris.add(r.getURI());
            collector.addNewUri(uri, new CrawleableUri(new URI(r.getURI())));
        }

        Iterator<byte[]> iterator = collector.getUris(uri);
        int cont =0;
        
        CrawleableUri curi = null;
        
        while(iterator.hasNext()) {
        	curi = serializer.deserialize(iterator.next());
        	cont++;
        }
        
        System.out.println(cont);


    }

}
