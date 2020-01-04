package org.dice_research.squirrel.sink;

import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;



public class HDFSSinkHelper {

    public boolean placeFileIntoHDFS(String srcFilePath) {
        boolean successFlag = true;
        String srcfileName = srcFilePath;

        String hdfsLocaHostURI = "hdfs://localhost:50070/";

        String desthdfsDirectory = "hdfs://localhost:50070/user/";

        Configuration conf = new Configuration();
        conf.addResource(new Path("file:///etc/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("file:///etc/hadoop/conf/hdfs-site.xml"));
        Path pSrc = new Path(srcfileName);
        Path pDst = new Path(desthdfsDirectory);
        try {
            FileSystem fs = FileSystem.get(URI.create(hdfsLocaHostURI),conf);
            fs.copyFromLocalFile(pSrc, pDst);
        } catch (IOException e) {
            successFlag = false;
            e.printStackTrace();
        }

        return successFlag;
    }
}
