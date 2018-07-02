package com;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A class, that handle the converting from a byteStream (e.g. that, what the consumer get from the rabbitMQ)) in a {@link SquirrelWebObject}
 * @author Philipp Heinisch
 */
@Deprecated
public abstract class SquirrelWebObjectHelper {

    /**
     * converts a byte stream into a {@link SquirrelWebObject}. If there are any exceptions, the methods tries to handle them
     * it works, but use e.g. squirrel/data/uri/serialize/java/GzipJavaUriSerializer.java instead
     * @param bytes the byte stream
     * @return a {@link SquirrelWebObject}, that was in further times converted into a byte stream
     */
    @SuppressWarnings("all")
    public static SquirrelWebObject convertToObject(byte[] bytes) {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return (SquirrelWebObject) o.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return new SquirrelWebObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
            SquirrelWebObject ret = new SquirrelWebObject();
            try {
                ret.setPendingURIs(new ArrayList<>(Collections.singleton("ERROR while deserialize process: " + e.getMessage() + ". The corrupted byte stream is: " + bytes)));
            } catch (IllegalAccessException e1) { }
            return ret;
        }
    }
}
