package org.dice_research.squirrel;

import static junit.framework.TestCase.assertTrue;

import java.util.List;

import org.junit.Test;

public class MongoDBMockTest extends MongoDBBasedTest {
	
	  @Test
	    public void test() {
		  mongoDB = client.getDatabase("testDB");
		  mongoDB.createCollection("testCollection");
		  List<String> dbList = client.getDatabaseNames();
		  assertTrue(dbList.contains("testDB"));
		  mongoDB.drop();
	    }

}
