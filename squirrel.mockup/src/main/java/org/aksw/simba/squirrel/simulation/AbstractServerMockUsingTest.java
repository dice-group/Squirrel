package org.aksw.simba.squirrel.simulation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.junit.After;
import org.junit.Before;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class AbstractServerMockUsingTest {

    public static final int SERVER_PORT = 8089;
    public static final String HTTP_SERVER_ADDRESS = "http://localhost:" + AbstractServerMockUsingTest.SERVER_PORT;
    
    protected Container container;
    protected Server server;
    protected Connection connection;
    
    public AbstractServerMockUsingTest(Container container) {
        this.container = container;
    }

    @Before
    public void startServer() throws IOException {
        server = new ContainerServer(container);
        connection = new SocketConnection(server);
        SocketAddress address = new InetSocketAddress(SERVER_PORT);
        connection.connect(address);
    }

    @After
    public void stopServer() throws IOException {
        connection.close();
        server.stop();
    }

}
