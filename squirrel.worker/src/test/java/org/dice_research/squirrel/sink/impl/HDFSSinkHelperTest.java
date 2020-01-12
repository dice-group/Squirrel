package org.dice_research.squirrel.sink.impl;


import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.dice_research.squirrel.configurator.HDFSSinkHelperConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dice_research.squirrel.sink.HDFSSinkHelper;
import java.io.File;


import java.io.IOException;
import java.net.URI;

public class HDFSSinkHelperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSSinkHelperTest.class);

    private static final long WAITING_TIME_BETWEEN_TRIPLES = 100;

    private HDFSSinkHelper hdfsSinkHelper = null;
    private String hdfsHost;
    private String destinationDirectory;

    @Before
    public void initialize() {
        hdfsSinkHelper = new HDFSSinkHelper();
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

    }

    @Test
    public void testPlaceFileIntoHDFS() {
        File oldLocal = new File(getClass().getClassLoader().getResource("sample.ttl").getFile());
        File newLocal = new File(getClass().getClassLoader().getResource("sample_hdfs.ttl").getFile());
        hdfsSinkHelper.placeFileIntoHDFS(oldLocal.getAbsolutePath());
        String desthdfsDirectory = hdfsHost + "/" + destinationDirectory + "/";
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS",hdfsHost);
        Path plocalSrc = new Path(newLocal.getAbsolutePath());
        Path pHDFSsrc = new Path(desthdfsDirectory);
        try {
            FileSystem fs = FileSystem.get(URI.create(hdfsHost),conf);
            if(fs.exists(pHDFSsrc)){
                fs.copyToLocalFile(true,pHDFSsrc,plocalSrc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            boolean isTwoEqual = FileUtils.contentEquals(oldLocal, newLocal);
            Assert.assertTrue(isTwoEqual);
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }


    }
}
