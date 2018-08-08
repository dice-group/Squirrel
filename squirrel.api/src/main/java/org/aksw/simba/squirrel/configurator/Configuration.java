package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;

import java.util.Map;

public class Configuration {
    private static Map<String, String> env = System.getenv();

    public static String getEnv(String envVariableName, Logger logger) {
        if (env.containsKey(envVariableName)) {
            return env.get(envVariableName);
        }
        logger.warn("Couldn't get {} from the environment.", envVariableName);
        return null;
    }

    public static int getEnvInteger(String envVariableName, Logger logger) {
        String toInt = getEnv(envVariableName, logger);

        try {
            return Integer.parseInt(toInt);
        } catch (Exception e) {
            logger.error(envVariableName + " not found.", e);
            return 0;
        }
    }

    public static boolean getEnvBoolean(String envVariableName, Logger logger) {
        String toBool = getEnv(envVariableName, logger);
        try {
            return Boolean.parseBoolean(toBool);
        } catch (Exception e) {
            logger.error(envVariableName + " not found.", e);
            return false;
        }
    }

    public static long getEnvLong(String envVariableName, Logger logger) {
        String toLong = getEnv(envVariableName, logger);
        try {
            return toLong != null ? Long.parseLong(toLong) : 0L;
        } catch (Exception e) {
            logger.error(envVariableName + " not found.", e);
            return 0L;
        }
    }


}
