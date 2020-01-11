package org.dice_research.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFSSinkHelperConfiguration extends Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSSinkHelperConfiguration.class);

    public static final String HDFS_REMOTE_KEY = "HDFS_REMOTE_HOST";
    public static final String HDFS_DESTINATION_DIRECTORY_KEY = "HDFS_DESTINATION_DIRECTORY";

    private String hdfsHost;
    private String destinationDirectory;

    public HDFSSinkHelperConfiguration(String remotehost, String destdirectory) {
        this.hdfsHost = remotehost;
        this.destinationDirectory = destdirectory;
    }

    public static HDFSSinkHelperConfiguration getHDFSHelperConfiguration() throws Exception {
        String hdfshost = getEnv(HDFS_REMOTE_KEY, LOGGER);
        String destinationdirectory = getEnv(HDFS_DESTINATION_DIRECTORY_KEY, LOGGER);
        if (hdfshost != null) {
            LOGGER.info("The worker will use " + HDFS_REMOTE_KEY + " as the HDFS host.");
            LOGGER.info("The worker will use " + HDFS_DESTINATION_DIRECTORY_KEY + " as the output directory.");
            return new HDFSSinkHelperConfiguration(hdfshost, destinationdirectory);
        } else {
            String msg = "Couldn't get " + HDFS_REMOTE_KEY + " from the environment. "+
                "Couldn't get " + HDFS_DESTINATION_DIRECTORY_KEY + " from the environment. ";
            LOGGER.error(msg);
            throw new Exception(msg);
        }
    }

    public String getHDFSHost(){
        return hdfsHost;
    }

    public String getDestinationdirectory() {
        return destinationDirectory;
    }
}

