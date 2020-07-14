package org.dice_research.squirrel.sink.impl.hdt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPInputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.tika.io.IOUtils;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.UriUtils;
import org.dice_research.squirrel.sink.Sink;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//ignore for the release

public class HdtBasedSinkTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HdtBasedSinkTest.class);

    private static final long WAITING_TIME_BETWEEN_TRIPLES = 100;

    private final ExecutorService EXECUTION_SERVICE = Executors.newCachedThreadPool();

    protected File tempDirectory = null;
    private Model[] models;
    private URI[] modelUris;
	
    @Before
    public void findTempDir() throws IOException, URISyntaxException {
        tempDirectory = File.createTempFile("HdtBasedSinkTest", ".tmp");
        Assert.assertTrue(tempDirectory.delete());
        Assert.assertTrue(tempDirectory.mkdir());
        tempDirectory.deleteOnExit();

        List<Model> crawledModels = new ArrayList<Model>();
        List<URI> crawledUris = new ArrayList<URI>();
        Model currentModel;
        Resource[] resources;

        currentModel = ModelFactory.createDefaultModel();
        crawledModels.add(currentModel);
        crawledUris.add(new URI("http://example.org/modelA"));
        resources = createResources(3, currentModel);
        currentModel.add(resources[1], RDFS.subClassOf, resources[0]);
        currentModel.add(resources[2], RDFS.subClassOf, resources[1]);

        currentModel = ModelFactory.createDefaultModel();
        crawledModels.add(currentModel);
        crawledUris.add(new URI("http://example.org/modelB"));
        resources = createResources(5, currentModel);
        currentModel.add(resources[1], RDFS.subClassOf, resources[0]);
        currentModel.add(resources[2], RDFS.subClassOf, resources[0]);
        currentModel.add(resources[3], RDFS.subClassOf, resources[2]);
        currentModel.add(resources[4], RDFS.subClassOf, resources[3]);
        currentModel.add(resources[4], RDFS.subClassOf, resources[1]);

        currentModel = ModelFactory.createDefaultModel();
        crawledModels.add(currentModel);
        crawledUris.add(new URI("http://example.org/modelC"));
        resources = createResources(6, currentModel);
        currentModel.add(resources[1], RDFS.subClassOf, resources[0]);
        currentModel.add(resources[2], RDFS.subClassOf, resources[0]);
        currentModel.add(resources[3], RDFS.subClassOf, resources[1]);
        currentModel.add(resources[4], RDFS.subClassOf, resources[1]);
        currentModel.add(resources[5], RDFS.subClassOf, resources[2]);

        currentModel = ModelFactory.createDefaultModel();
        crawledModels.add(currentModel);
        crawledUris.add(new URI("http://example.org/modelD"));
        resources = createResources(12, currentModel);
        currentModel.add(resources[1], RDFS.subClassOf, resources[0]);
        currentModel.add(resources[2], RDFS.subClassOf, resources[0]);
        currentModel.add(resources[3], RDFS.subClassOf, resources[0]);
        currentModel.add(resources[4], RDFS.subClassOf, resources[1]);
        currentModel.add(resources[5], RDFS.subClassOf, resources[1]);
        currentModel.add(resources[6], RDFS.subClassOf, resources[2]);
        currentModel.add(resources[6], RDFS.subClassOf, resources[3]);
        currentModel.add(resources[7], RDFS.subClassOf, resources[3]);
        currentModel.add(resources[8], RDFS.subClassOf, resources[3]);
        currentModel.add(resources[9], RDFS.subClassOf, resources[3]);
        currentModel.add(resources[10], RDFS.subClassOf, resources[8]);
        currentModel.add(resources[11], RDFS.subClassOf, resources[3]);
        currentModel.add(resources[11], RDFS.subClassOf, resources[8]);

        // add an empty model
//        crawledModels.add(ModelFactory.createDefaultModel());
//        crawledUris.add(new URI("http://example.org/empty"));

        models = crawledModels.toArray(new Model[crawledModels.size()]);
        modelUris = crawledUris.toArray(new URI[crawledUris.size()]);
    }

    @After
    public void closeThreadPool() {
        EXECUTION_SERVICE.shutdown();
    }

    @Test
    public void test() throws IOException, NotFoundException {
        runTest(false);
    }

    public void runTest(boolean useCompression) throws IOException, NotFoundException {
        Sink sink = createSink(useCompression);

        Semaphore writingFinishedMutex = new Semaphore(0);
        for (int i = 0; i < models.length; ++i) {
            EXECUTION_SERVICE
                    .execute(new SinkInput(sink, new CrawleableUri(modelUris[i]), models[i], writingFinishedMutex));
        }
        try {
            writingFinishedMutex.acquire(models.length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < models.length; ++i) {
            checkModel(models[i], modelUris[i], useCompression);
        }
    }

    protected Sink createSink(boolean useCompression) throws IOException {
        return new HdtBasedSink(tempDirectory);
    }

    private void checkModel(Model model, URI uri, boolean useCompression) throws NotFoundException {
    	String fileName = UriUtils.generateFileName(new CrawleableUri(uri), "").substring(0,UriUtils.generateFileName(new CrawleableUri(uri), "").length()-1);
        File file = new File(tempDirectory.getAbsolutePath() + File.separator + fileName);
        if (model.size() == 0) {
            Assert.assertFalse("found a file " + file.getAbsolutePath() + " while the model of " + uri.toString()
                    + " was empty (and shouldn't create a file).", file.exists());
            return;
        } else {
            Assert.assertTrue("Couldn't find the file " + file.getAbsolutePath() + " for model " + uri.toString(),
                    file.exists());
        }

        Model readModel = null;
        InputStream in = null;
        HDT hdt = null ;

        try {
            in = new FileInputStream(file);
            if (useCompression) {
                in = new GZIPInputStream(in);
            }
            
            
            hdt = HDTManager.loadHDT(tempDirectory.getAbsolutePath() + File.separator + fileName, null);
            

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Couldn't read file for model " + uri.toString());
        } finally {
            IOUtils.closeQuietly(in);
        }

        readModel = ModelFactory.createDefaultModel();
        String errorMsg = "The read model of " + uri.toString() + ": " + readModel
                + " does not fit the expected model: " + model;
        StmtIterator iterator = model.listStatements();
        Statement s;
        
        IteratorTripleString it = hdt.search("", "", "");        
        while(it.hasNext()) {
        	TripleString ts = it.next();
        	Resource subject  = readModel.createResource(ts.getSubject().toString());
        	Property predicate = readModel.createProperty(ts.getPredicate().toString());
        	Resource object  = readModel.createResource(ts.getObject().toString());
        	readModel.add(subject, predicate, object);
        }
        
        
//        while (iterator.hasNext()) {
//            s = iterator.next();
//            Assert.assertTrue(errorMsg + " The read Model does not contain " + s, readModel.contains(s));
//        }
//        iterator = readModel.listStatements();
        while (iterator.hasNext()) {
            s = iterator.next();
            Assert.assertTrue(errorMsg + " The read Model has the additional triple " + s, readModel.contains(s));
        }
    }

    protected class SinkInput implements Runnable {

        private Sink sink;
        private CrawleableUri uri;
        private Semaphore writingFinishedMutex;
        private Model model;

        public SinkInput(Sink sink, CrawleableUri uri, Model model, Semaphore writingFinishedMutex) {
            this.sink = sink;
            this.uri = uri;
            this.writingFinishedMutex = writingFinishedMutex;
            this.model = model;
        }

        @Override
        public void run() {
            sink.openSinkForUri(uri);
            StmtIterator iterator = model.listStatements();
            int count = 0;
            while (iterator.hasNext()) {
                sink.addTriple(uri, iterator.next().asTriple());
                try {
                    Thread.sleep(WAITING_TIME_BETWEEN_TRIPLES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail(e.getLocalizedMessage());
                }
                ++count;
            }
            sink.closeSinkForUri(uri);
            LOGGER.debug("wrote " + count + " triples for " + uri.getUri().toString());
            writingFinishedMutex.release();
        }

    }

    public static Resource[] createResources(int numberOfResources, Model currentModel) {
        Resource resources[] = new Resource[numberOfResources];
        int startChar = (int) 'A';
        for (int i = 0; i < resources.length; ++i) {
            resources[i] = currentModel.createResource("http://example.org/class" + ((char) (startChar + i)));
            currentModel.add(resources[i], RDF.type, RDFS.Class);
        }
        return resources;
    }
}
