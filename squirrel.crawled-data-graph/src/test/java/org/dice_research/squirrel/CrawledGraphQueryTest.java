package org.dice_research.squirrel;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrawledGraphQueryTest {


    public GraphDatabaseService graphDb;
    /**
     * Create temporary database for each unit test.
     */
    // tag::beforeTest[]
    @Before
    public void prepareTestDatabase()
    {
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File("src/test/resources/graph.db"));
    }

    // end::beforeTest[]


    @Test
    public void getGraph()
    {

        List<String> domains = new ArrayList<>();
        domains.add("dbpedia.org");
        domains.add("dbpedia.org");
        domains.add("dbpedia.org");
        domains.add("wikidata.org");
        domains.add("example.com");
        domains.add("example.com");
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("plDomain", "dbpedia.org");
        parameters.put("plDomain", "wikidata.org");
        parameters.put("plDomain", "example.com");
        while(parameters.containsKey("plDomain")) {

            try (Driver driver = GraphDatabase
                .driver(String.valueOf(graphDb.beginTx()), Config.build().withoutEncryption().toConfig())) {
                Session session = driver.session();
                String cypherQuery = "CREATE (domain:name {id:$plDomain,triple:$triples})\n" +
                    "SET domain.label = $plDomain\n" +
                    "WITH domain \n" +
                    "OPTIONAL MATCH (domain)<-[r:triples]-(n)\n" +
                    "WITH domain,n,r \n" +
                    "DELETE r \n";

                StatementResult result = session.run(cypherQuery, parameters);
                Assert.assertTrue("", true);
            }
        }

    }

    /**
     * Shutdown the database.
     */
    // tag::afterTest[]
    @After
    public void destroyTestDatabase()
    {
        graphDb.shutdown();
    }
    // end::afterTest[]


}
