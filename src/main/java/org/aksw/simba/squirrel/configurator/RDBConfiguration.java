package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RDBConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBConfiguration.class);

    private String RDBHostName = null;
    private Integer RDBPort = null;

    private static Map<String, String> env = System.getenv();

    private static final String RDB_HOST_NAME_KEY = "RDB_HOST_NAME";
    private static final String RDB_PORT_KEY = "RDB_PORT";

    private RDBConfiguration(String RDBHostName, Integer RDBPort) {
        this.RDBHostName = RDBHostName;
        this.RDBPort = RDBPort;
    }

    public static RDBConfiguration getRDBConfiguration() {
        String RDBHostName = getEnvRDBHostName();
        Integer RDBPort = getEnvRDBPort();
        if(RDBHostName != null && RDBPort != null) {
            return new RDBConfiguration(RDBHostName, RDBPort);
        } else {
            return null;
        }
    }

    public static String getEnvRDBHostName() {
        if (env.containsKey(RDB_HOST_NAME_KEY)) {
            return env.get(RDB_HOST_NAME_KEY);
        }
        LOGGER.warn("Couldn't get {} from the environment.", RDB_HOST_NAME_KEY);
        return null;
    }

    public static Integer getEnvRDBPort() {
        if(env.containsKey(RDB_PORT_KEY)) {
            return Integer.parseInt(env.get(RDB_PORT_KEY));
        }
        LOGGER.warn("Couldn't get {} from the environment.", RDB_PORT_KEY);
        return null;
    }

    public String getRDBHostName() {
        return RDBHostName;
    }

    public Integer getRDBPort() {
        return RDBPort;
    }
}
