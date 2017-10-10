package org.aksw.simba.squirrel.simulation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.aksw.simba.squirrel.AbstractServerMockUsingTest;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.filter.InMemoryKnownUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.impl.InMemorySink;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;

@RunWith(Parameterized.class)
public class ScenarioBasedTest extends AbstractServerMockUsingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioBasedTest.class);

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        List<Object[]> scenarios = new ArrayList<Object[]>();
        CrawleableUriFactory uriFactory = new CrawleableUriFactoryImpl();
        String server1Url = "http://127.0.0.1:" + SERVER_PORT;
        String server2Url = "http://127.0.0.2:" + SERVER_PORT;
        String server3Url = "http://127.0.0.3:" + SERVER_PORT;

        Model model1, model2, model3;
        /*
         * Simple scenario in which resource1 is the seed and points to
         * resource2 which points to resource3.
         */
        model1 = ModelFactory.createDefaultModel();
        model1.add(model1.createResource(server1Url + "/entity_1"), model1.createProperty(server2Url + "/property_1"),
                model1.createLiteral("literal"));
        model2 = ModelFactory.createDefaultModel();
        model2.add(model2.createResource(server1Url + "/entity_1"), model2.createProperty(server2Url + "/property_1"),
                model2.createResource(server3Url + "/entity_2"));
        model3 = ModelFactory.createDefaultModel();
        model3.add(model3.createResource(server1Url + "/entity_2"), model3.createProperty(server2Url + "/property_1"),
                model3.createLiteral("literal2"));
        scenarios.add(new Object[] {
                new CrawleableUri[] { uriFactory.create(new URI(server1Url + "/entity_1"), UriType.DEREFERENCEABLE) },
                new CrawleableResource[] { new StringResource(model1, server1Url + "/entity_1", Lang.N3),
                        new StringResource(model2, server2Url + "/property_1", Lang.N3),
                        new StringResource(model3, server3Url + "/entity_2", Lang.N3) } });

        /*
         * The same scenario with different serializations.
         */
        model1 = ModelFactory.createDefaultModel();
        model1.add(model1.createResource(server1Url + "/entity_1"), model1.createProperty(server2Url + "/property_1"),
                model1.createLiteral("literal"));
        model2 = ModelFactory.createDefaultModel();
        model2.add(model2.createResource(server1Url + "/entity_1"), model2.createProperty(server2Url + "/property_1"),
                model2.createResource(server3Url + "/entity_2"));
        model3 = ModelFactory.createDefaultModel();
        model3.add(model3.createResource(server1Url + "/entity_2"), model3.createProperty(server2Url + "/property_1"),
                model3.createLiteral("literal2"));
        scenarios.add(new Object[] {
                new CrawleableUri[] { uriFactory.create(new URI(server1Url + "/entity_1"), UriType.DEREFERENCEABLE) },
                new CrawleableResource[] { new StringResource(model1, server1Url + "/entity_1", Lang.RDFXML),
                        new StringResource(model2, server2Url + "/property_1", Lang.TURTLE),
                        new StringResource(model3, server3Url + "/entity_2", Lang.RDFJSON) } });

        /*
         * Example in which the dump fetcher needs to be able to read the data
         * like a normal fetcher.
         */
        model1 = ModelFactory.createDefaultModel();
        model1.add(model1.createResource(server1Url + "/entity_1.n3"),
                model1.createProperty(server2Url + "/property_1.n3"), model1.createLiteral("literal"));
        model2 = ModelFactory.createDefaultModel();
        model2.add(model2.createResource(server1Url + "/entity_1.n3"),
                model2.createProperty(server2Url + "/property_1.n3"),
                model2.createResource(server3Url + "/entity_2.n3"));
        model3 = ModelFactory.createDefaultModel();
        model3.add(model3.createResource(server1Url + "/entity_2.n3"),
                model3.createProperty(server2Url + "/property_1.n3"), model3.createLiteral("literal2"));
        scenarios.add(new Object[] {
                new CrawleableUri[] {
                        uriFactory.create(new URI(server1Url + "/entity_1.n3"), UriType.DUMP) },
                new CrawleableResource[] { new StringResource(model1, server1Url + "/entity_1.n3", Lang.N3),
                        new StringResource(model2, server2Url + "/property_1.n3", Lang.N3),
                        new StringResource(model3, server3Url + "/entity_2.n3", Lang.N3) } });
        return scenarios;
    }

    private CrawleableUri[] seeds;
    private CrawleableResource[] resources;

    public ScenarioBasedTest(CrawleableUri[] seeds, CrawleableResource[] resources) {
        super(new CrawleableResourceContainer(resources));
        this.resources = resources;
        this.seeds = seeds;
    }

    @Test
    public void test() {
        Frontier frontier = new FrontierImpl(new InMemoryKnownUriFilter(100000), new InMemoryQueue());
        InMemorySink sink = new InMemorySink();
        WorkerImpl worker = new WorkerImpl(frontier, sink,
                new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), 100);

        for (int i = 0; i < seeds.length; ++i) {
            frontier.addNewUri(seeds[i]);
        }

        Thread t = new Thread(worker);
        t.start();
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Assert.fail(e.getLocalizedMessage());
            }
            Assert.assertTrue("The worker crashed", t.isAlive());
        } while ((frontier.getNumberOfPendingUris() > 0));
        worker.setTerminateFlag(true);

        // compare the expected results with those found inside the sink
        boolean success = true;
        Map<String, Model> crawledResources = sink.getCrawledResources();
        for (int i = 0; i < resources.length; ++i) {
            if (crawledResources.containsKey(resources[i].getResourceName())) {
                success &= compareModels(resources[i].getResourceName(), resources[i].getModel(),
                        crawledResources.get(resources[i].getResourceName()));
            } else {
                LOGGER.error("The resource \"" + resources[i].getResourceName() + "\" has not been crawled.");
                success = false;
            }
        }
        Assert.assertTrue(success);
        Assert.assertTrue("The sink is not healthy!", sink.isSinkHealthy());
    }

    private boolean compareModels(String resourceName, Model expModel, Model carwledModel) {
        StmtIterator iterator = expModel.listStatements();
        Statement statement;
        boolean modelsAreEqual = true;
        while (iterator.hasNext()) {
            statement = iterator.next();
            if (!carwledModel.contains(statement)) {
                LOGGER.error("The crawled model of \"" + resourceName + "\" does not contain the expected statement "
                        + statement.toString());
                modelsAreEqual = false;
            }
        }
        iterator = carwledModel.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            if (!expModel.contains(statement)) {
                LOGGER.error("The crawled model of \"" + resourceName + "\" contains the unexpected statement "
                        + statement.toString());
                modelsAreEqual = false;
            }
        }
        return modelsAreEqual;
    }

}
