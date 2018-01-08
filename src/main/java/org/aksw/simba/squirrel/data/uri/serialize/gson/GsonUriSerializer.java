package org.aksw.simba.squirrel.data.uri.serialize.gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ByteArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * A serializer that uses {@link Gson} to serialize URIs. Kept for backwards
 * compatibility.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class GsonUriSerializer implements Serializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonUriSerializer.class);

    private GsonBuilder builder;
    private Gson gson;

    public GsonUriSerializer() {
        this(new GsonBuilder());
    }

    public GsonUriSerializer(GsonBuilder builder) {
        this.builder = builder;
        this.builder.registerTypeAdapter(CrawleableUri.class, new CrawleableUriAdapter());
        gson = this.builder.create();
    }

    @Override
    public <T> byte[] serialize(T object) {
        return gson.toJson(object).getBytes(Constants.DEFAULT_CHARSET);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data) {
        return (T) gson.fromJson(new String(data, Constants.DEFAULT_CHARSET), CrawleableUri.class);
    }

    private class CrawleableUriAdapter extends TypeAdapter<CrawleableUri> {

        private static final String URI_KEY = "uri";
        private static final String ADDRESS_KEY = "address";
        private static final String ADDRESS_IP_KEY = "ip";
        private static final String ADDRESS_HOST_KEY = "host";
        private static final String URI_TYPE_KEY = "type";
        private static final String DATA_KEY = "data";
        private static final String DATA_NAME_KEY = "name";
        private static final String DATA_VALUE_KEY = "value";
        private static final String DATA_VALUE_TYPE_KEY = "type";

        @SuppressWarnings("deprecation")
        @Override
        public void write(JsonWriter out, CrawleableUri uri) throws IOException {
            out.beginObject();
            out.name(URI_KEY);
            out.value(uri.getUri().toString());
            out.name(URI_TYPE_KEY);
            out.value(uri.getType().name());
            if (uri.getIpAddress() != null) {
                out.name(ADDRESS_KEY);
                writeInetAddress(out, uri.getIpAddress());
            }
            out.name(DATA_KEY);
            out.beginArray();
            Map<String, Object> data = uri.getData();
            for (String key : data.keySet()) {
                writeDataEntry(out, key, data.get(key));
            }
            out.endArray();
            out.endObject();
        }

        private void writeInetAddress(JsonWriter out, InetAddress ipAddress) throws IOException {
            out.beginObject();
            if (ipAddress.getHostName() != null) {
                out.name(ADDRESS_HOST_KEY);
                out.value(ipAddress.getHostName());
            }
            out.name(ADDRESS_IP_KEY);
            byte ip[] = ipAddress.getAddress();
            out.beginArray();
            for (int i = 0; i < ip.length; ++i) {
                out.value(ip[i]);
            }
            out.endArray();
            out.endObject();
        }

        private void writeDataEntry(JsonWriter out, String key, Object value) throws IOException {
            out.beginObject();
            out.name(DATA_NAME_KEY);
            out.value(key);
            out.name(DATA_VALUE_TYPE_KEY);
            out.value(value.getClass().getCanonicalName());
            out.name(DATA_VALUE_KEY);
            // use the gson instance of our surrounding class to serialize the
            // object
            out.jsonValue(gson.toJson(value));
            out.endObject();
        }

        @SuppressWarnings("deprecation")
        @Override
        public CrawleableUri read(JsonReader in) throws IOException {
            in.beginObject();
            String uri = null;
            String key;
            InetAddress inetAddress = null;
            UriType type = UriType.UNKNOWN;
            Map<String, Object> data = new HashMap<String, Object>();
            while (in.peek() != JsonToken.END_OBJECT) {
                key = in.nextName();
                switch (key) {
                case URI_KEY: {
                    uri = in.nextString();
                    break;
                }
                case URI_TYPE_KEY: {
                    type = UriType.valueOf(in.nextString());
                    break;
                }
                case ADDRESS_KEY: {
                    inetAddress = readInetAddress(in);
                    break;
                }
                case DATA_KEY: {
                    in.beginArray();
                    while (in.hasNext()) {
                        readDataObject(in, data);
                    }
                    in.endArray();
                    break;
                }
                default: {
                    LOGGER.error(
                            "Got an unknown attribute name \"{}\" while parsing an CrawleableUri object. It will be ignored.",
                            key);
                }
                }
            }
            in.endObject();
            CrawleableUri result;
            try {
                result = new CrawleableUri(new URI(uri), inetAddress, type);
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
            result.setData(data);
            return result;
        }

        private InetAddress readInetAddress(JsonReader in) throws IOException {
            in.beginObject();
            String host = null;
            String key;
            ByteArrayList ip = new ByteArrayList();
            while (in.peek() != JsonToken.END_OBJECT) {
                key = in.nextName();
                switch (key) {
                case ADDRESS_HOST_KEY: {
                    host = in.nextString();
                    break;
                }
                case ADDRESS_IP_KEY: {
                    in.beginArray();
                    while (in.hasNext()) {
                        ip.add((byte) in.nextLong());
                    }
                    in.endArray();
                    break;
                }
                default: {
                    LOGGER.error(
                            "Got an unknown attribute name \"{}\" while parsing an InetAddress object. It will be ignored.",
                            key);
                }
                }
            }
            in.endObject();
            if (host != null) {
                return InetAddress.getByAddress(host, ip.toArray());
            } else {
                return InetAddress.getByAddress(ip.toArray());
            }
        }

        private void readDataObject(JsonReader in, Map<String, Object> data) throws IOException {
            in.beginObject();
            String key;
            String name = null;
            String valueType = null;
            Object value = null;
            while (in.peek() != JsonToken.END_OBJECT) {
                key = in.nextName();
                switch (key) {
                case DATA_NAME_KEY: {
                    name = in.nextString();
                    break;
                }
                case DATA_VALUE_TYPE_KEY: {
                    valueType = in.nextString();
                    break;
                }
                case DATA_VALUE_KEY: {
                    if (valueType != null) {
                        try {
                            value = gson.fromJson(in, Class.forName(valueType));
                        } catch (ClassNotFoundException e) {
                            throw new IOException(e);
                        }
                    } else {
                        LOGGER.error(
                                "Couldn't read Object of {} because the value type was not defined before reading the value. It will be skipped.",
                                name);
                        in.skipValue();
                    }
                    break;
                }
                default: {
                    LOGGER.error("Got an unknown attribute name \"{}\" while parsing an object. It will be ignored.",
                            key);
                }
                }
            }
            if ((name != null) && (value != null)) {
                data.put(name, value);
            }
            in.endObject();
        }

    }
}
