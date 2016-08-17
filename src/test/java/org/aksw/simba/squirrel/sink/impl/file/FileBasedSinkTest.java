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
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.tika.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class FileBasedSinkTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedSinkTest.class);

	private static final long WAITING_TIME_BETWEEN_TRIPLES = 100;
	private static final ExecutorService EXECUTION_SERVICE = Executors.newCachedThreadPool();

	private static File tempDirectory = null;
	private static Model[] models;
	private static URI[] modelUris;

	@BeforeClass
	public static void findTempDir() throws IOException, URISyntaxException {
		File tempFile = File.createTempFile("FileBasedSinkTest", ".tmp");
		tempDirectory = tempFile.getAbsoluteFile().getParentFile();

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

	@AfterClass
	public static void closeThreadPool() {
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
		FileBasedSink sink = new FileBasedSink(tempDirectory, useCompression);

		Semaphore writingFinishedMutex = new Semaphore(0);
		for (int i = 0; i < models.length; ++i) {
			EXECUTION_SERVICE.execute(new SinkInput(sink, new CrawleableUri(modelUris[i]), models[i],
					writingFinishedMutex));
		}
		try {
			writingFinishedMutex.acquire(models.length);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			// break to make sure that the written files are available
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

		for (int i = 0; i < models.length; ++i) {
			checkModel(models[i], modelUris[i], useCompression);
		}
	}

	private void checkModel(Model model, URI uri, boolean useCompression) {
		String fileName = FileBasedSink.generateFileName(uri.toString(), useCompression);
		File file = new File(tempDirectory.getAbsolutePath() + File.separator + fileName);
		Assert.assertTrue("Couldn't find the file " + file.getAbsolutePath() + " for model " + uri.toString(),
				file.exists());

		Model readModel = null;
		Dataset readData = DatasetFactory.createMem();
		InputStream in = null;

		try {
			in = new FileInputStream(file);
			if (useCompression) {
				in = new GZIPInputStream(in);
			}
			RDFDataMgr.read(readData, in, Lang.NQ);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Couldn't read file for model " + uri.toString());
		} finally {
			IOUtils.closeQuietly(in);
		}

		readModel = readData.getNamedModel(uri.toString());
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
