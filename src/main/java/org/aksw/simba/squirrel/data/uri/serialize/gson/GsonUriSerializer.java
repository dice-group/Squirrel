package org.aksw.simba.squirrel.data.uri.serialize.gson;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.CrawleableUriSerializer;

import com.google.gson.Gson;

/**
 * A serializer that uses {@link Gson} to serialize URIs. Kept for backwards
 * compatibility.
 * 
 * @deprecated This serialization has no typing which leads to problems when it
 *             has to serialize and deserialize the internal
 *             {@code Map<String, Object>} of the given Uris.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Deprecated
public class GsonUriSerializer implements CrawleableUriSerializer {

    private Gson gson = new Gson();

    @Override
    public byte[] serialize(CrawleableUri uri) {
        return gson.toJson(uri).getBytes(Constants.DEFAULT_CHARSET);
    }

    @Override
    public CrawleableUri deserialize(byte[] data) {
        return gson.fromJson(new String(data, Constants.DEFAULT_CHARSET), CrawleableUri.class);
    }

}
