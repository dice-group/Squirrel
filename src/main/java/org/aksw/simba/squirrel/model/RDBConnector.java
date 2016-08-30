package org.aksw.simba.squirrel.model;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

/**
 * Created by ivan on 8/30/16.
 */
public class RDBConnector {
    Connection connection = null;
    RethinkDB r = RethinkDB.r;
    String hostname;
    Integer port;

    public RDBConnector(String hostname, Integer port){
        this.hostname = hostname;
        this.port = port;
    }

    public void open() {
        connection = r.connection().hostname(hostname).port(port).connect();
    }

    public void close() {
        connection.close();
    }

}
