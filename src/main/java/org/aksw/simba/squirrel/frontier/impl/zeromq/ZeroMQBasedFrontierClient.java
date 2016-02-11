package org.aksw.simba.squirrel.frontier.impl.zeromq;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.zeromq.utils.ZeroMQUtils;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class ZeroMQBasedFrontierClient implements Frontier, Closeable {

    private static final String WORKER_IDENTITY_STRING = "Worker#";

    public static ZeroMQBasedFrontierClient create(String frontierAddress, int workerId) {
        ZMQ.Context context = ZMQ.context(1);
        Socket clientSocket = context.socket(ZMQ.REQ);
        clientSocket.connect(frontierAddress);
        setIdentity(clientSocket, workerId);
        return new ZeroMQBasedFrontierClient(clientSocket);
    }

    protected static void setIdentity(Socket clientSocket, int workerId) {
        String identity = WORKER_IDENTITY_STRING + workerId;
        try {
            clientSocket.setIdentity(identity.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            clientSocket.setIdentity(identity.getBytes());
        }
    }

    protected ZMQ.Socket clientSocket;
    protected Queue<List<CrawleableUri>> localQueue = new LinkedList<List<CrawleableUri>>();

    protected ZeroMQBasedFrontierClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public List<CrawleableUri> getNextUris() {
        if (localQueue.size() == 0) {
            requestUris();
        }
        return localQueue.poll();
    }

    private void requestUris() {
        clientSocket.send(ZeroMQBasedFrontier.READY_SIGNAL);
        receiveResponse();
    }

    @Override
    public void addNewUri(CrawleableUri uri) {
        ZeroMQUtils.sendUris(clientSocket, uri);
        receiveResponse();
    }

    @Override
    public void addNewUris(List<CrawleableUri> uris) {
        ZeroMQUtils.sendUris(clientSocket, uris);
        receiveResponse();
    }

    @Override
    public void crawlingDone(List<CrawleableUri> crawledUris, List<CrawleableUri> newUris) {
        ZeroMQUtils.sendUriListPair(clientSocket, crawledUris, newUris);
        receiveResponse();
    }

    protected void receiveResponse() {
        byte data[] = clientSocket.recv();
        if (ZeroMQUtils.arraysEqual(data, ZeroMQBasedFrontier.TERMINATE_SIGNAL)) {
            // TODO we should be able to terminate the worker...
        } else if (!ZeroMQUtils.arraysEqual(data, ZeroMQBasedFrontier.WAIT_SIGNAL)) {
            List<CrawleableUri> uris = ZeroMQUtils.parseUris(ByteBuffer.wrap(data));
            if (uris != null) {
                localQueue.add(uris);
            }
        }
    }

    @Override
    public void close() throws IOException {
        clientSocket.close();
    }

    @Override
    public int getNumberOfPendingUris() {
        return 0;
    }

}
