package org.aksw.simba.squirrel.data.uri.serialize.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.xerial.snappy.Snappy;

public class SnappyJavaUriSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        if (object instanceof Serializable) {
            return toString((Serializable) object);
        } else {
            throw new IllegalArgumentException("The given instance of " + object.getClass().getCanonicalName()
                    + " does not implement the java.io.Serializable interface. This serializer does not support that.");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data) throws IOException {
        try {
            return (T) fromString(data);
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    private static byte[] toString(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        String stringToSend = Base64.getEncoder().encodeToString(baos.toByteArray());
        byte[] bytesToSend = stringToSend.getBytes();
        return Snappy.compress(bytesToSend);
    }

    private static Object fromString(byte[] compressedString) throws IOException, ClassNotFoundException {
        byte[] uncompressedString = Snappy.uncompress(compressedString);
        String receivedString = new String(uncompressedString);
        byte[] data = Base64.getDecoder().decode(receivedString);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

}
