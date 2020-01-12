package org.dice_research.squirrel.sink.impl.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.configurator.HDFSSinkConfiguration;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HDFSSink extends FileBasedSink{

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSSink.class);
    private final ExecutorService EXECUTION_SERVICE = Executors.newScheduledThreadPool(100);
    private String hdfsHost;
    private String destinationDirectory;

    public HDFSSink(File outputDirectory, boolean useCompression) {
        super(outputDirectory, useCompression);
    }

    public HDFSSink(File outputDirectory, Lang outputLang, boolean useCompression) {
        super(outputDirectory, outputLang, useCompression);
    }


    @Override
    public void addMetaData(Model model) {
        CrawleableUri uri = new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI);
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            addTriple(uri, iterator.next().asTriple());
        }
        placeFileIntoHDFS((String)uri.getData(Constants.HDFS_SOURCE_FILE));
    }

    public void placeFileIntoHDFS(String srcFilePath) {
        try {
            hdfsHost = HDFSSinkConfiguration.getHDFSHelperConfiguration().getHDFSHost();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        try {
            destinationDirectory = HDFSSinkConfiguration.getHDFSHelperConfiguration().getDestinationdirectory();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        EXECUTION_SERVICE.execute(new AsyncTask(srcFilePath));
    }

    protected class AsyncTask implements Runnable{

        private String srcFilePath;
        public AsyncTask(String srcFilePath) {
            this.srcFilePath = srcFilePath;
        }

        @Override
        public void run() {
            performAsyncAction();
        }


        public void performAsyncAction() {
            EXECUTION_SERVICE.shutdown();
            String desthdfsDirectory = hdfsHost + "/" + destinationDirectory + "/";

            Configuration conf = new Configuration();
            /*
             core-site.xml file should have the <Namenode-Host> and <Port> with cluster namenode and Port
             */
            conf.set("fs.defaultFS",hdfsHost);

            Path pSrc = new Path(srcFilePath);
            Path pDst = new Path(desthdfsDirectory);
            try {
                FileSystem fs = FileSystem.get(URI.create(hdfsHost),conf);
                fs.copyFromLocalFile(pSrc, pDst);
            } catch (IOException e) {
                LOGGER.error("",e);
            }
        }
    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {
        try{
            if (!EXECUTION_SERVICE.awaitTermination(60, TimeUnit.SECONDS)) {
                EXECUTION_SERVICE.shutdownNow();
                if (!EXECUTION_SERVICE.awaitTermination(1, TimeUnit.SECONDS)){
                    LOGGER.error("Execution Service of HDFS file copy did not terminate");
                }
            }
        }catch (InterruptedException ie) {
            EXECUTION_SERVICE.shutdownNow();
        }
        super.closeSinkForUri(uri);
    }
}
