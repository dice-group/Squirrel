package org.aksw.simba.squirrel;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class RethinkDBMockTest extends TestCase {
    public RethinkDB r;
    public Connection connection;

    public final static String DB_HOST_NAME = "localhost";
    public final static int DB_PORT = 58015;

    public void setUp() throws IOException, InterruptedException {
        String rethinkDockerExecCmd = "docker run --name squirrel-test-rethinkdb " +
            "-p 58015:28015 -p 58887:8080 -d rethinkdb";
        Process p = Runtime.getRuntime().exec(rethinkDockerExecCmd);
        BufferedReader stdInput = new BufferedReader(new
            InputStreamReader(p.getInputStream()));
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // read any errors from the attempted command
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null)
        {
            System.out.println(s);
        }

        r = RethinkDB.r;
        int retryCount = 0;
        while(true) {
            try {
                connection = r.connection().hostname(DB_HOST_NAME).port(DB_PORT).connect();
                break;
            } catch(ReqlDriverError error) {
                System.out.println("Could not connect, retrying");
                retryCount++;
                if(retryCount > 10) break;
                Thread.sleep(5000);
            }
        }
    }

    public void test() {
        r.dbCreate("testDb").run(connection);
        List<String> dbList = r.dbList().run(connection);
        assertTrue(dbList.contains("testDb"));
        r.dbDrop("testDb");
    }

    public void tearDown() throws IOException {
        String rethinkDockerStopCommand = "docker stop squirrel-test-rethinkdb";
        Process p = Runtime.getRuntime().exec(rethinkDockerStopCommand);
        String rethinkDockerRmCommand = "docker rm squirrel-test-rethinkdb";
        p = Runtime.getRuntime().exec(rethinkDockerRmCommand);
    }
}
