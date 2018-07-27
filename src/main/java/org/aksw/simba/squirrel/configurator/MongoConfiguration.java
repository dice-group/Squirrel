package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MongoConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfiguration.class);

    private String MongoHostName = null;
    private Integer MongoPort = null;

    private static final String MDB_HOST_NAME_KEY = "MDB_HOST_NAME";
    private static final String MDB_PORT_KEY = "MDB_PORT";

    private MongoConfiguration(String MDBHostName, Integer MDBPort) {
        this.MongoHostName = MDBHostName;
        this.MongoPort = MDBPort;
    }

    public static MongoConfiguration getMDBConfiguration() {
        String RDBHostName = getEnvMDBHostName();
        Integer RDBPort = getEnvMDBPort();
        if(RDBHostName != null && RDBPort != null) {
            return new MongoConfiguration(RDBHostName, RDBPort);
        } else {
            return null;
        }
    }

    private static String getEnvMDBHostName() {
        return getEnv(MDB_HOST_NAME_KEY, LOGGER);
    }

    private static Integer getEnvMDBPort() {
        return getEnvInteger(MDB_PORT_KEY, LOGGER);
    }

    public String getMDBHostName() {
        return MongoHostName;
    }

    public Integer getMDBPort() {
        return MongoPort;
    }
}
