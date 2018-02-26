package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;

import java.text.ParseException;
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

    public static Integer getEnvInteger(String envVariableName, Logger logger) {
        String toInt = getEnv(envVariableName, logger);
        if(toInt != null) {
            return Integer.parseInt(toInt);
        }
        return null;
    }

    public static boolean getEnvBoolean(String envVariableName, Logger logger) throws ParseException {
        String toBool = getEnv(envVariableName, logger);
        if(toBool != null) {
            return Boolean.parseBoolean(toBool);
        }
        throwParseException(toBool, "boolean");
        return false;
    }

    public static Long getEnvLong(String envVariableName, Logger logger) {
        String toLong = getEnv(envVariableName, logger);
        if(toLong != null) {
            return Long.parseLong(toLong);
        }
        return null;
    }

    private static void throwParseException(String value, String type) throws ParseException {
        String exceptionText = String.format("Could not parse %s value: %s", type, value);
        Integer errorOffset = 0;
        throw new ParseException(exceptionText, errorOffset);
    }
}
