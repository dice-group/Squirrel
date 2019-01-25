package org.dice_research.squirrel.fetcher.http;

import org.dice_research.squirrel.*;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.CrawleableUriFactory;
import org.dice_research.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.dice_research.squirrel.data.uri.UriType;
import org.dice_research.squirrel.simulation.AbstractServerMockUsingTest;
import org.dice_research.squirrel.simulation.CrawleableResource;
import org.dice_research.squirrel.simulation.CrawleableResourceContainer;
import org.dice_research.squirrel.simulation.StringResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.simpleframework.http.core.Container;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class HTTPFetcherTest extends AbstractServerMockUsingTest {


    private CrawleableUri[] seeds;
    private CrawleableResource[] resources;
   public HTTPFetcherTest(CrawleableUri[] seeds, CrawleableResource[] resources) {
        super(new CrawleableResourceContainer(resources));
        this.resources = resources;
        this.seeds = seeds;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        List<Object[]> scenarios = new ArrayList<Object[]>();
        CrawleableUriFactory uriFactory = new CrawleableUriFactoryImpl();
        String server1Url = "http://127.0.0.1:" + SERVER_PORT;

        Model model1;

        model1 = ModelFactory.createDefaultModel();
        model1.add(model1.createResource(server1Url + "/entity_1.n3"),
            model1.createProperty(server1Url + "/property_1.n3"), model1.createLiteral("literal"));
        //generating crawlable URI objects for two classes
        scenarios.add(new Object[]{
            new CrawleableUri[]{uriFactory.create(new URI(server1Url + "/entity_1.n3"), UriType.DUMP)},
            new CrawleableResource[]{new StringResource(model1, server1Url + "/entity_1.n3", Lang.N3)}});
        return scenarios;
    }

    @Test
    public void fetch() throws Exception {
        int expected_file_content = 110;
        HTTPFetcher fetcher = new HTTPFetcher();
        //checks the crawlable uri object of first class
            File data = fetcher.fetch(seeds[0]);
            assertNotNull(data); //checks that the file has been fetched
            assertEquals(expected_file_content,data.length()); //checks that the fetched file is not empty and has the expeceted content
        //checks the crawlable uri object of second class
            File resourceData1 = fetcher.fetch(new CrawleableUri(new URI(resources[0].getResourceName())));
            assertNotNull(resourceData1);
            assertEquals(expected_file_content,resourceData1.length());
    }
}
