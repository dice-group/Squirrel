package org.dice_research.squirrel.sink;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.dice_research.squirrel.configurator.HDFSSinkHelperConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HDFSSinkHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSSinkHelper.class);
    private final ExecutorService EXECUTION_SERVICE = Executors.newScheduledThreadPool(100);
    private String hdfsHost;
    private String destinationDirectory;

    public void placeFileIntoHDFS(String srcFilePath) {
        try {
            hdfsHost = HDFSSinkHelperConfiguration.getHDFSHelperConfiguration().getHDFSHost();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        try {
            destinationDirectory = HDFSSinkHelperConfiguration.getHDFSHelperConfiguration().getDestinationdirectory();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        EXECUTION_SERVICE.execute(new AsyncTask(srcFilePath));
        try {
            EXECUTION_SERVICE.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("",e);
        }
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
}
