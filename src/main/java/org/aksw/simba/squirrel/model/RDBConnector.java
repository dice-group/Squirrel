package org.aksw.simba.squirrel.model;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.aksw.simba.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ivan on 8/30/16.
 */
public class RDBConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDBKnownUriFilter.class);

    public Connection connection = null;
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

    public boolean databaseExists(String database) {
        List<String> databaseList = r.dbList().run(connection);
        return databaseList.contains(database);
    }

    public boolean squirrelDatabaseExists() {
        return databaseExists("squirrel");
    }

    public boolean tableExists(String database, String table) {
        List<String> tableList = r.db(database)
            .tableList()
            .run(connection);
        return tableList.contains(table);
    }

}
