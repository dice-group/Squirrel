package org.dice_research.squirrel.worker.impl;

import com.mongodb.MongoClient;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.analyzer.manager.SimpleOrderedAnalyzerManager;
import org.dice_research.squirrel.collect.SqlBasedUriCollector;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.dice_research.squirrel.data.uri.UriType;
import org.dice_research.squirrel.data.uri.filter.MongoDBKnowUriFilter;
import org.dice_research.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.dice_research.squirrel.data.uri.norm.NormalizerImpl;
import org.dice_research.squirrel.frontier.impl.FrontierImpl;
import org.dice_research.squirrel.queue.MongoDBQueue;
import org.dice_research.squirrel.queue.RDBQueue;
import org.dice_research.squirrel.robots.RobotsManagerImpl;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.utils.TempFileHelper;
import com.rethinkdb.gen.exc.ReqlDriverError;
import org.junit.*;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.dice_research.squirrel.sink.impl.file.FileBasedSink;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class WorkerImplTest {

    public static final String DB_HOST_NAME = "localhost";
    public static final int DB_PORT = 58015;
    static FrontierImpl frontier;
    static MongoDBQueue queue;
    static MongoDBKnowUriFilter filter;
    static List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    protected File tempDirectory = null;
    static CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();
    private static  RethinkDB r;
    private static Connection connection;
    protected static  MongoClient client;
    File  outputDir = null ;
    WorkerImpl worker;

    @BeforeClass
    public static void setUpMDB() throws Exception {

        String mongoDockerStopCommand = "docker stop squirrel-test-mongodb";
        Process p = Runtime.getRuntime().exec(mongoDockerStopCommand);
        p.waitFor();
        String mongoDockerRmCommand = "docker rm squirrel-test-mongodb";
        p = Runtime.getRuntime().exec(mongoDockerRmCommand);
        p.waitFor();
        String mongoDockerExecCmd = "docker run --name squirrel-test-mongodb "
            + "-p 58027:27017 -p 58886:8080 -d mongo:4.0.0";
         p = Runtime.getRuntime().exec(mongoDockerExecCmd);
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

        client = new MongoClient(DB_HOST_NAME,DB_PORT);

    }




    @Test
    public void test() throws Exception{
        filter = new MongoDBKnowUriFilter("localhost", 58027);
        queue = new MongoDBQueue("localhost", 58027);
        filter.open();
        queue.open();
        frontier = new FrontierImpl(new NormalizerImpl(), filter, queue,true);
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/New_York"), InetAddress.getByName("127.0.0.1"),
            UriType.DEREFERENCEABLE));
        //uris.add(cuf.create(new URI("http://dbpedia.org/resource/Moscow"), InetAddress.getByName("127.0.0.1"),
          //  UriType.DEREFERENCEABLE));
        queue.addCrawleableUri(uris.get(1));
      //  queue.addCrawleableUri(uris.get(2));
        tempDirectory = File.createTempFile("FileBasedSinkTest", ".tmp");
        Assert.assertTrue(tempDirectory.delete());
        Assert.assertTrue(tempDirectory.mkdir());
        tempDirectory.deleteOnExit();
       // Sink sink = new FileBasedSink(outputDir, true) ;
        Sink sink = new FileBasedSink(tempDirectory, FileBasedSink.DEFAULT_OUTPUT_LANG, true);
        Serializer serializer = new GzipJavaUriSerializer();
        String dbdir = null;
        dbdir = TempFileHelper.getTempDir("dbTest", "").getAbsolutePath() + File.separator + "test";
        UriCollector collector = SqlBasedUriCollector.create(serializer, dbdir) ;
       Analyzer analyzer = new SimpleOrderedAnalyzerManager(collector);
        worker = new WorkerImpl(frontier,sink,analyzer,new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))),
            serializer, collector, 100, null, true);
        worker.run();
        System.out.println("done");
    }



    @AfterClass
    public static void tearDownMDB() throws Exception {
        String mongoDockerStopCommand = "docker stop squirrel-test-mongodb";
        Process p = Runtime.getRuntime().exec(mongoDockerStopCommand);
        p.waitFor();
        String mongoDockerRmCommand = "docker rm squirrel-test-mongodb";
        p = Runtime.getRuntime().exec(mongoDockerRmCommand);
        p.waitFor();
    }
}
