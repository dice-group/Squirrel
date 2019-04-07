package org.dice_research.squirrel.worker.standalone;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.analyzer.impl.MicrodataParser;
import org.dice_research.squirrel.analyzer.manager.SimpleOrderedAnalyzerManager;
import org.dice_research.squirrel.collect.SimpleUriCollector;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.frontier.Frontier;
import org.dice_research.squirrel.robots.RobotsManager;
import org.dice_research.squirrel.robots.RobotsManagerImpl;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.sink.impl.mem.InMemorySink;
import org.dice_research.squirrel.worker.impl.WorkerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;

public class CommandLineWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineWorker.class);

    public void startWorker(String uriToCrawl) throws Exception{

        WorkerImpl worker;
        Frontier frontier;
        Sink sink;
        Analyzer analyzer;
        RobotsManager manager;
        Serializer serializer;
        UriCollector uriCollector;

        CrawleableUri uri = new CrawleableUri(new URI(uriToCrawl));
        sink = new SinkStandAlone();
        serializer = new GzipJavaUriSerializer();
        uriCollector = new SimpleUriCollector(serializer);
        analyzer = new SimpleOrderedAnalyzerManager(uriCollector);
        frontier = new FrontierCommandLine();
        manager = new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent(Constants.DEFAULT_USER_AGENT, "", "")));
        worker = new WorkerImpl(frontier,
            sink,
            analyzer,
            manager,
            serializer,
            uriCollector,
            2000,
            "/var/squirrel/data" + File.separator + "log",
            true
        );
        worker.performCrawling(uri);
    }

    public static void main(String... args){
        try {
            CommandLineWorker commandLineWorker = new CommandLineWorker();
            commandLineWorker.startWorker("https://data.cambridgeshireinsight.org.uk/search/field_topics/type/dataset");
        } catch (Exception e){
            LOGGER.error("Exception - " + e);
        }
    }
}
