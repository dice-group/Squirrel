package org.dice_research.squirrel;



import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class CrawledGraph
{
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecFactoryConnection.class);


    public static void main(String[] args) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("cgraph", "param"));
        try (Session session = driver.session()) {
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            String cypherQuery=
                "CREATE (m1:data1 { Node1:$nameOfDomain}),(m2:data2 {Node2:$baseDomain}),(m3:data3 {Node3:$subDomain}), " +
                    "(m4:data4 {Node4:$path}),(m5:data5 {Node5:$value}),(m6:data6 {Node6:$uri})  "+
                    "CREATE (m1)-[r1:baseDomain]->(m2), (m1)-[r2:subDomain]->(m3), (m1)-[r3:path]->(m4), (m1)-[r4:value]->(m5), (m1)-[r5:uri]->(m6)\n" +
                    "RETURN m1,m2,m3,m4,m5,m6,r1,r2,r3,r4,r5";
            StatementResult result = session.run(cypherQuery, parameters);
        }
        driver.close();
    }

}




