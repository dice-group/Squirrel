package org.dice_research.squirrel;



import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class CrawledGraph
{
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecFactoryConnection.class);
    private static final File databaseDirectory = new File( "squirrel.crawled-data-graph/src/main/resources/neo4j-graph-db" );

    public String data;
    static GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );;
    Node firstNode;
    Node secondNode;
    Relationship relationship;

    private enum RelTypes implements RelationshipType
    {
        KNOWS
    }

    void createDb() throws IOException
    {
        FileUtils.deleteRecursively( databaseDirectory );
        registerShutdownHook( graphDb );
        try ( Transaction tx = graphDb.beginTx() )
        {
            //Database operations go here
            //addData
            firstNode = graphDb.createNode();
            firstNode.setProperty( "message", "dbpedia, " );
            secondNode = graphDb.createNode();
            secondNode.setProperty( "message", "wiki" );

            relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
            relationship.setProperty( "message", "domain " );

            //readData
            System.out.print( firstNode.getProperty( "message" ) );
            System.out.print( relationship.getProperty( "message" ) );
            System.out.print( secondNode.getProperty( "message" ) );

            data = ( (String) firstNode.getProperty( "message" ) )
                + ( (String) relationship.getProperty( "message" ) )
                + ( (String) secondNode.getProperty( "message" ) );

            tx.success();
        }
    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    public static void main(String[] args){
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("cgraph", "neo4j"));
        try (Session session = driver.session()) {
            StatementResult result = session.run("CREATE (database:Database {name:\"dbpedia\"})-[r:domain]->(message:Message {name:\"wiki\"}) " +
                "RETURN database, message, r");
            CrawledGraph crawledGraph = new CrawledGraph();
            crawledGraph.createDb();

        } catch (IOException e) {
            e.printStackTrace();
        }
        driver.close();
    }

}
