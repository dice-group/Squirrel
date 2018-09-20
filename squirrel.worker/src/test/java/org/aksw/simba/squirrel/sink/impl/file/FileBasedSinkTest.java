package org.aksw.simba.squirrel.sink.impl.file;

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

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.tika.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBasedSinkTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedSinkTest.class);

    private static final long WAITING_TIME_BETWEEN_TRIPLES = 100;

    private final ExecutorService EXECUTION_SERVICE = Executors.newCachedThreadPool();

    protected File tempDirectory = null;
    private Model[] models;
    private URI[] modelUris;

    @Before
    public void findTempDir() throws IOException, URISyntaxException {
        tempDirectory = File.createTempFile("FileBasedSinkTest", ".tmp");
        Assert.assertTrue(tempDirectory.delete());
        Assert.assertTrue(tempDirectory.mkdir());
        tempDirectory.deleteOnExit();
        LOGGER.info("Using " + tempDirectory.getAbsolutePath());

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
        crawledModels.add(ModelFactory.createDefaultModel());
        crawledUris.add(new URI("http://example.org/empty"));

        models = crawledModels.toArray(new Model[crawledModels.size()]);
        modelUris = crawledUris.toArray(new URI[crawledUris.size()]);
    }

    @After
    public void closeThreadPool() {
        EXECUTION_SERVICE.shutdown();
    }

    @Test
    public void test() {
        runTest(false);
    }

    @Test
    public void testWithCompression() {
        runTest(true);
    }

    public void runTest(boolean useCompression) {
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

    protected Sink createSink(boolean useCompression) {
        return new FileBasedSink(tempDirectory, FileBasedSink.DEFAULT_OUTPUT_LANG, useCompression);
    }

    private void checkModel(Model model, URI uri, boolean useCompression) {
        String fileName = FileBasedSink.generateFileName(uri.toString(), FileBasedSink.DEFAULT_OUTPUT_LANG, useCompression);
        File file = new File(tempDirectory.getAbsolutePath() + File.separator + fileName);
        if (model.size() == 0) {
            Assert.assertFalse("found a file " + file.getAbsolutePath() + " while the model of " + uri.toString()
                    + " was empty (and shouldn't create a file).", file.exists());
            return;
        } else {
            Assert.assertTrue("Couldn't find the file " + file.getAbsolutePath() + " for model " + uri.toString(),
                    file.exists());
        }

        Model readModel = ModelFactory.createDefaultModel();
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            if (useCompression) {
                in = new GZIPInputStream(in);
            }
            RDFDataMgr.read(readModel, in, FileBasedSink.DEFAULT_OUTPUT_LANG);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Couldn't read file for model " + uri.toString());
        } finally {
            IOUtils.closeQuietly(in);
        }

        String errorMsg = "The read model of " + uri.toString() + ": " + readModel
                + " does not fit the expected model: " + model;
        StmtIterator iterator = model.listStatements();
        Statement s;
        while (iterator.hasNext()) {
            s = iterator.next();
            Assert.assertTrue(errorMsg + " The read Model does not contain " + s, readModel.contains(s));
        }
        iterator = readModel.listStatements();
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