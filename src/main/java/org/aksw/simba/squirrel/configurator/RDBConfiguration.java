package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDBConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBConfiguration.class);

    private String RDBHostName = null;
    private Integer RDBPort = null;

    private static final String RDB_HOST_NAME_KEY = "RDB_HOST_NAME";
    private static final String RDB_PORT_KEY = "RDB_PORT";

    private RDBConfiguration(String RDBHostName, Integer RDBPort) {
        this.RDBHostName = RDBHostName;
        this.RDBPort = RDBPort;
    }

    public static RDBConfiguration getRDBConfiguration() {
        String RDBHostName = getEnvRDBHostName();
        Integer RDBPort = getEnvRDBPort();
        if (RDBHostName != null && RDBPort != null) {
            return new RDBConfiguration(RDBHostName, RDBPort);
        } else {
            return null;
        }
    }

    private static String getEnvRDBHostName() {
        return getEnv(RDB_HOST_NAME_KEY, LOGGER);
    }

    private static Integer getEnvRDBPort() {
        return getEnvInteger(RDB_PORT_KEY, LOGGER);
    }

    public String getRDBHostName() {
        return RDBHostName;
    }

    public Integer getRDBPort() {
        return RDBPort;
    }
}
