package org.dice_research.squirrel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.*;

public class CrawledGraphQueryTest {
    QueryExecFactoryConnection queryExecFactoryConnection = new QueryExecFactoryConnection();
    Driver driver;
    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withConfig( GraphDatabaseSettings.auth_enabled, "true" );

    @Before
    public void prepare()
    {
        driver = GraphDatabase.driver( neo4j.boltURI(), AuthTokens.basic( "cgraph", "param"  ), Config.build().withoutEncryption().toConfig() );
    }

   // @Rule
   // public Neo4jRule neo4j = new Neo4jRule().withProcedure(CrawledGraphQueryTest.class);

    //@Test
    public void getGraph() {
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
                .driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {
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
}
