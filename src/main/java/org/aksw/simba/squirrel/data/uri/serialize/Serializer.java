package org.aksw.simba.squirrel.data.uri.serialize;

import java.io.IOException;

public interface Serializer {

    public <T> byte[] serialize(T object) throws IOException;

    public default <T> byte[] serializeSafely(T object) {
        try {
            return serialize(object);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T deserialize(byte[] data) throws IOException;

    public default <T> T deserializeSafely(byte[] data) {
        try {
            return deserialize(data);
        } catch (Exception e) {
            return null;
        }
    }
}
