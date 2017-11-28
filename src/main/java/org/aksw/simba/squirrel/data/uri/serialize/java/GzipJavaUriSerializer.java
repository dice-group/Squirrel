package org.aksw.simba.squirrel.data.uri.serialize.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.apache.commons.io.IOUtils;

public class GzipJavaUriSerializer implements Serializer {

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
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new GZIPOutputStream(baos));
            os.writeObject(obj);
        } finally {
            IOUtils.closeQuietly(os);
        }
        return baos.toByteArray();
    }

    private static Object fromString(byte[] compressedString) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(compressedString)));
            Object o = ois.readObject();
            ois.close();
            return o;
        } finally {
            IOUtils.closeQuietly(ois);
        }
    }

}
