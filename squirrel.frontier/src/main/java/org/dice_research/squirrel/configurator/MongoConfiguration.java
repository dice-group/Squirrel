package org.dice_research.squirrel.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfiguration.class);

    private String MongoHostName = null;
    private Integer MongoPort = null;
    private Integer connectionTimeout = null;
    private Integer socketTimeout = null;
    private Integer serverTimeout = null;

    private static final String MDB_HOST_NAME_KEY = "MDB_HOST_NAME";
    private static final String MDB_PORT_KEY = "MDB_PORT";
    private static final String CONNECTION_TIME_OUT_MS = "MDB_CONNECTION_TIME_OUT";
    private static final String SOCKET_TIME_OUT_MS = "MDB_SOCKET_TIME_OUT";
    private static final String SERVER_SELECTION_TIMEOUT_MS = "MDB_SERVER_TIME_OUT";


    private MongoConfiguration(String MDBHostName, Integer MDBPort,Integer connectionTimeout, Integer socketTimeout,Integer serverTimeout) {
        this.MongoHostName = MDBHostName;
        this.MongoPort = MDBPort;
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
        this.serverTimeout = serverTimeout;
    }
    
    private MongoConfiguration(String MDBHostName, Integer MDBPort) {
        this.MongoHostName = MDBHostName;
        this.MongoPort = MDBPort;
    }

    public static MongoConfiguration getMDBConfiguration() {
        String MDBHostName = getEnvMDBHostName();
        Integer MDBPort = getEnvMDBPort();
        Integer connectionTimeout = getCon_Timeout();
        Integer socketTimeout = getSoc_Timeout();
        Integer serverTimeout = getSrv_Timeout();


        if(MDBHostName != null && MDBPort != 0 && connectionTimeout != 0 && socketTimeout != 0 && serverTimeout != 0) {
            return new MongoConfiguration(MDBHostName, MDBPort,connectionTimeout,socketTimeout,serverTimeout);
        } else if(MDBHostName != null && MDBPort != null){
        	return new MongoConfiguration(MDBHostName, MDBPort);
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
    
    private static Integer getCon_Timeout() {
        return getEnvInteger(CONNECTION_TIME_OUT_MS, LOGGER);

    }
    
    private static Integer getSoc_Timeout() {
        return getEnvInteger(SOCKET_TIME_OUT_MS, LOGGER);

    }
    
    private static Integer getSrv_Timeout() {
        return getEnvInteger(SERVER_SELECTION_TIMEOUT_MS, LOGGER);

    }

    public String getMDBHostName() {
        return MongoHostName;
    }

    public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public Integer getSocketTimeout() {
		return socketTimeout;
	}

	public Integer getServerTimeout() {
		return serverTimeout;
	}

	public Integer getMDBPort() {
        return MongoPort;
    }
}
