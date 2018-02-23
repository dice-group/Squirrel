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
        String exceptionText = String.format("Could not parse boolean value: %s", toBool);
        Integer errorOffset = 0;
        throw new ParseException(exceptionText, errorOffset);
    }
}
