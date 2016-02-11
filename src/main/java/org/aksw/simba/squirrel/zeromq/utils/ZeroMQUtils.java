package org.aksw.simba.squirrel.zeromq.utils;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.commons.collections.Pair;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

public class ZeroMQUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroMQUtils.class);

    public static final int URI_PAIR_SENTINEL_LENGTH = -1;

    public static List<CrawleableUri> receiveUris(ZMQ.Socket socket) {
        byte bytes[] = socket.recv();
        if (bytes == null) {
            return null;
        }
        try {
            return parseUris(ByteBuffer.wrap(bytes));
        } catch (BufferUnderflowException e) {
            LOGGER.error("Exception while parsing list of URIs. Returning null.", e);
            return null;
        }
    }

    public static List<CrawleableUri> parseUris(ByteBuffer buffer) throws BufferUnderflowException {
        return receiveUris(buffer, buffer.getInt());
    }

    protected static List<CrawleableUri> receiveUris(ByteBuffer buffer, int listLength)
            throws BufferUnderflowException {
        List<CrawleableUri> uris = new ArrayList<CrawleableUri>(listLength);
        CrawleableUri parsedUri;
        for (int i = 0; i < listLength; ++i) {
            parsedUri = CrawleableUri.fromByteBuffer(buffer);
            if (parsedUri != null) {
                uris.add(parsedUri);
            }
        }
        return uris;
    }

    public static void sendUris(ZMQ.Socket socket, byte address[], List<CrawleableUri> uris) {
        socket.sendMore(address);
        socket.sendMore(new byte[0]);
        sendUris(socket, uris);
    }

    public static void sendUris(ZMQ.Socket socket, CrawleableUri... uris) {
        sendUris(socket, Arrays.asList(uris));
    }

    public static void sendUris(ZMQ.Socket socket, List<CrawleableUri> uris) {
        socket.send(generateUriListArray(uris).array());
    }

    public static ByteBuffer generateUriListArray(List<CrawleableUri> uris) {
        List<byte[]> byteArrays = new ArrayList<byte[]>(uris.size());
        byte bytes[];
        int lengthSum = 0;
        for (CrawleableUri uri : uris) {
            if (uri != null) {
                bytes = uri.toByteArray();
                if (bytes != null) {
                    lengthSum += bytes.length;
                    byteArrays.add(bytes);
                }
            }
        }
        ByteBuffer buffer = ByteBuffer.allocate(lengthSum + 4);
        buffer.putInt(byteArrays.size());
        for (byte array[] : byteArrays) {
            buffer.put(array);
        }
        return buffer;
    }

    public static Pair<List<CrawleableUri>, List<CrawleableUri>> receiveUriListPair(ZMQ.Socket socket) {
        byte bytes[] = socket.recv();
        if (bytes == null) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return parseUriListPair(buffer);
    }

    public static Pair<List<CrawleableUri>, List<CrawleableUri>> parseUriListPair(ByteBuffer buffer) {
        int sentinelLength = buffer.getInt();
        if (sentinelLength != URI_PAIR_SENTINEL_LENGTH) {
            // we expected a list pair but there is only one single list
            return new Pair<List<CrawleableUri>, List<CrawleableUri>>(receiveUris(buffer, sentinelLength), null);
        }
        List<CrawleableUri> firstList = parseUris(buffer);
        if (firstList == null) {
            return null;
        }
        return new Pair<List<CrawleableUri>, List<CrawleableUri>>(firstList, parseUris(buffer));
    }

    public static void sendUriListPair(ZMQ.Socket socket, List<CrawleableUri> list1, List<CrawleableUri> list2) {
        socket.send(generateUriListPairArray(list1, list2).array());
    }

    protected static ByteBuffer generateUriListPairArray(List<CrawleableUri> list1, List<CrawleableUri> list2) {
        List<byte[]> byteArrays1 = new ArrayList<byte[]>(list1.size());
        List<byte[]> byteArrays2 = new ArrayList<byte[]>(list2.size());
        byte bytes[];
        int lengthSum = 0;
        for (CrawleableUri uri : list1) {
            if (uri != null) {
                bytes = uri.toByteArray();
                if (bytes != null) {
                    lengthSum += bytes.length;
                    byteArrays1.add(bytes);
                }
            }
        }
        for (CrawleableUri uri : list2) {
            if (uri != null) {
                bytes = uri.toByteArray();
                if (bytes != null) {
                    lengthSum += bytes.length;
                    byteArrays2.add(bytes);
                }
            }
        }
        ByteBuffer buffer = ByteBuffer.allocate(lengthSum + 12);
        buffer.putInt(URI_PAIR_SENTINEL_LENGTH);
        buffer.putInt(byteArrays1.size());
        for (byte array[] : byteArrays1) {
            buffer.put(array);
        }
        buffer.putInt(byteArrays2.size());
        for (byte array[] : byteArrays2) {
            buffer.put(array);
        }
        return buffer;
    }

    public static boolean arraysEqual(byte array1[], byte array2[]) {
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; ++i) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
}
