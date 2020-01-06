package org.dice_research.squirrel.sink.impl;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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

    @Before
    public void findTempDir() {
        hdfsSinkHelper = new HDFSSinkHelper();
    }

    @Test
    public void testPlaceFileIntoHDFS() {
        String srcFilePath = "dummfile.txt";
        File n = new File(srcFilePath);

        try {
            n.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        boolean retrunValue = hdfsSinkHelper.placeFileIntoHDFS("");
        Assert.assertTrue(retrunValue);
        String hdfsLocaHostURI = "hdfs://localhost:50070/";
        String desthdfsDirectory = "hdfs://localhost:50070/user/dummyfile.txt";
        Configuration conf = new Configuration();
        conf.addResource(new Path("file:///etc/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("file:///etc/hadoop/conf/hdfs-site.xml"));
        Path pSrc = new Path(srcFilePath);
        Path pDst = new Path(desthdfsDirectory);
        try {
            FileSystem fs = FileSystem.get(URI.create(hdfsLocaHostURI),conf);
            Assert.assertTrue(fs.exists(pDst));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File n2 = new File(srcFilePath);
        n2.delete();
    }
}
