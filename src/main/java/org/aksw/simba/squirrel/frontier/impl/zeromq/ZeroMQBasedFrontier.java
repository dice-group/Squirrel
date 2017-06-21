package org.aksw.simba.squirrel.frontier.impl.zeromq;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.aksw.commons.collections.Pair;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.zeromq.utils.ZeroMQUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

@Deprecated
public class ZeroMQBasedFrontier implements Closeable, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroMQBasedFrontier.class);

    private static final long DEFAULT_SLEEP_DURATION = 2000;

    public static final byte READY_SIGNAL[] = new byte[] { 1 };
    public static final byte WAIT_SIGNAL[] = new byte[] { 2 };
    public static final byte TERMINATE_SIGNAL[] = new byte[] { 3 };

    public static ZeroMQBasedFrontier create(Frontier frontier, String frontierAddress) {
        Context context = ZMQ.context(1);
        Socket serverSocket = context.socket(ZMQ.ROUTER);
        serverSocket.bind(frontierAddress);// "ipc://frontier.ipc"

        return new ZeroMQBasedFrontier(serverSocket, frontier);
    }

    protected ZMQ.Socket serverSocket;
    protected Frontier frontier;
    protected boolean running;
    protected long sleepDuration = DEFAULT_SLEEP_DURATION;

    protected ZeroMQBasedFrontier(Socket serverSocket, Frontier frontier) {
        this.serverSocket = serverSocket;
        this.frontier = frontier;
    }

    @Override
    public void run() {
        running = true;
        List<CrawleableUri> uris;
        Pair<List<CrawleableUri>, List<CrawleableUri>> receivedListPair;

        LOGGER.debug("Frontier start running...");
        byte[] addressFrame, emptyFrame, dataFrame;
        boolean messageFromWorker;
        while (running) {
            // listen for workers
            PollItem items[] = { new PollItem(serverSocket, Poller.POLLIN) };
            int rc = ZMQ.poll(items, 1000);
            if (rc == -1)
                break;// Interrupted

            // Handle incoming status messages
            if (items[0].isReadable()) {
                addressFrame = serverSocket.recv();
                emptyFrame = serverSocket.recv();
                dataFrame = serverSocket.recv();
                if ((addressFrame != null) && (emptyFrame != null) && (dataFrame != null)) {
                    messageFromWorker = true;
                    // If the worker does not send a simple READY signal
                    if (!ZeroMQUtils.arraysEqual(READY_SIGNAL, dataFrame)) {
                        // Check for URI lists
                        receivedListPair = ZeroMQUtils.parseUriListPair(ByteBuffer.wrap(dataFrame));
                        if (receivedListPair != null) {
                            LOGGER.debug("Received new URIs from the workers/seed generator!");
                            // Check whether we got one or two lists
                            if (receivedListPair.second != null) {
                                // the first list contains URIs that have been
                                // crawled while the second list contains new
                                // URIs
                                frontier.crawlingDone(receivedListPair.first, receivedListPair.second);
                            } else {
                                // the second list is null, we got a single URI
                                // list that should be added to the frontier
                                LOGGER.debug("Adding new URIs to the queue...");
                                frontier.addNewUris(receivedListPair.first);
                                messageFromWorker = false;
                            }
                        }
                    }
                    if (messageFromWorker) {
                        // Generate a URI list and send it to the worker
                        uris = frontier.getNextUris();
                        if (uris != null) {
                            ZeroMQUtils.sendUris(serverSocket, addressFrame, uris);
                        } else {
                            serverSocket.sendMore(addressFrame);
                            serverSocket.sendMore(new byte[0]);
                            // Check whether we can expect new URIs in the
                            // future
                            if (frontier.getNumberOfPendingUris() != 0) {
                                serverSocket.send(TERMINATE_SIGNAL);
                            } else {
                                serverSocket.send(WAIT_SIGNAL);
                            }
                        }
                    } else {
                        // respond with the terminate signal to the seed
                        // generator
                        serverSocket.send(TERMINATE_SIGNAL);
                    }
                } else {
                    LOGGER.error("One of the received frames is null. addressFrame={} emptyFrame={} dataFrame={}",
                            addressFrame, emptyFrame, dataFrame);
                }
            }
        }
        try {
            close();
        } catch (IOException e) {
            LOGGER.error("Exception while closing sockets.");
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }

    public Frontier getFrontier() {
        return frontier;
    }

    public void setFrontier(Frontier frontier) {
        this.frontier = frontier;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        LOGGER.debug(String.format("Running is set to %s", running));
        this.running = running;
    }

}
