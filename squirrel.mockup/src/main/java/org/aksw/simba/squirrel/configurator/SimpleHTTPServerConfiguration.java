package org.aksw.simba.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SimpleHTTPServerConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHTTPServerConfiguration.class);

    private static Map<String, String> env = System.getenv();

    private static final String MODEL_KEY = "RESOURCE_MODEL";
    private static final String MODEL_LANG_KEY = "RESOURCE_MODEL_LANG";
    private static final String SERVER_PORT_KEY = "PORT";
    private static final String DUMP_FILE_NAME_KEY = "DUMP_FILE_NAME";
    private static final String USE_DEREF_KEY = "USE_DEREF";

    private String modelFile;
    private String modelLang;
    private Integer serverPort;
    private String dumpFileName;
    private boolean useDeref;

    private SimpleHTTPServerConfiguration(String modelFile,
                                          String modelLang,
                                          Integer serverPort,
                                          String dumpFileName,
                                          boolean useDeref) {
        this.modelFile = modelFile;
        this.modelLang = modelLang;
        this.serverPort = serverPort;
        this.dumpFileName = dumpFileName;
        this.useDeref = useDeref;
    }

    public static SimpleHTTPServerConfiguration getSimpleHTTPServerConfiguration() {
        return new SimpleHTTPServerConfiguration(
            getEnvModelFile(),
            getEnvLangKey(),
            getEnvServerPort(),
            getEnvDumpFileName(),
            getEnvDerefKey()
        );
    }

    private static String getEnvModelFile() {
        return getEnv(MODEL_KEY, LOGGER);
    }

    private static String getEnvLangKey() {
        return getEnv(MODEL_LANG_KEY, LOGGER);
    }

    private static Integer getEnvServerPort() {
        return getEnvInteger(SERVER_PORT_KEY, LOGGER);
    }

    private static String getEnvDumpFileName() {
        return getEnv(DUMP_FILE_NAME_KEY, LOGGER);
    }

    private static boolean getEnvDerefKey() {
        try {
            return getEnvBoolean(USE_DEREF_KEY, LOGGER);
        } catch(Exception e) {
            LOGGER.warn("Setting deref to true by default. ", e);
            return true;
        }
    }

    public String getModelFile() {
        return modelFile;
    }

    public String getModelLang() {
        return modelLang;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public String getDumpFileName() {
        return dumpFileName;
    }

    public boolean isUseDeref() {
        return useDeref;
    }
}
