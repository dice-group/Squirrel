package org.aksw.simba.squirrel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class MongoDBBasedTest {

    @BeforeClass
    public static void setUpRDB() throws Exception {
        String rethinkDockerExecCmd = "docker run --name squirrel-test-mongodb "
            + "-p 58027:27017 -p 58887:8080 -d mongo:4.0.0";
        Process p = Runtime.getRuntime().exec(rethinkDockerExecCmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // read any errors from the attempted command
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

    }

    @AfterClass
    public static void tearDownRDB() throws Exception {
        String rethinkDockerStopCommand = "docker stop squirrel-test-mongodb";
        Process p = Runtime.getRuntime().exec(rethinkDockerStopCommand);
        p.waitFor();
        String rethinkDockerRmCommand = "docker rm squirrel-test-mongodb";
        p = Runtime.getRuntime().exec(rethinkDockerRmCommand);
        p.waitFor();
    }
}
