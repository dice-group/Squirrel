package org.aksw.simba.squirrel.data.uri.serialize.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.CrawleableUriSerializer;
import org.xerial.snappy.Snappy;

public class SnappyJavaUriSerializer implements CrawleableUriSerializer {

    @Override
    public byte[] serialize(CrawleableUri uri) throws IOException {
        return toString(uri);
    }

    @Override
    public CrawleableUri deserialize(byte[] data) throws IOException {
        try {
            return (CrawleableUri) fromString(data);
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
