package org.dice_research.squirrel.data.uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class represents a URI and additional meta data that is helpful for
 * crawling it.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class CrawleableUri implements Serializable {


    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawleableUri.class);

    private static final String CHARSET_NAME = "UTF-8";
    private static final Charset ENCODING_CHARSET = Charset.forName(CHARSET_NAME);
    private static final int URI_START_INDEX = 5;
    /**
     * The URI.
     */
    private final URI uri;
    /**
     * The IP address of the URI.
     */
    private InetAddress ipAddress;
    @Deprecated
    private UriType type = UriType.UNKNOWN;
    /**
     * The data attached to this URI.
     */
    private Map<String, Object> data = new TreeMap<>();
    /**
     * Timestamp at which this URI should be crawled next time.
     * 
     * @deprecated The timestamp should be added to the {@link #data} map.
     */
    private long timestampNextCrawl;


    /**
     * Creates a CrawleableUri object from the given byte array.
     *
     * @param bytes
     * @return
     * @deprecated Use the JSON deserialization instead.
     */
    @Deprecated
    public static CrawleableUri fromByteArray(byte bytes[]) {
        if ((bytes == null) || (bytes.length < URI_START_INDEX)) {
            return null;
        }
        // We need the buffer to get int values from the byte array.
        return fromByteBuffer(ByteBuffer.wrap(bytes));
    }

    /**
     *
     * @param buffer
     * @return
     * @deprecated Use the JSON deserialization instead.
     */
    public static CrawleableUri fromByteBuffer(ByteBuffer buffer) {
        if ((buffer == null) || ((buffer.limit() - buffer.position()) < URI_START_INDEX)) {
            return null;
        }
        // We need the buffer to get int values from the byte array.
        int typeId = buffer.get();
        if ((typeId < 0) || (typeId >= UriType.values().length)) {
            LOGGER.error("Got an unknown URI type Id {}. Returning null.", typeId);
            return null;
        }
        int uriLength = buffer.getInt();
        int remainingBytes = (buffer.limit() - buffer.position());
        if (uriLength > remainingBytes) {
            LOGGER.error("Got a URI length {} that would exceed the byte array length. Returning null.", uriLength,
                    remainingBytes);
            return null;
        }
        // From now on, we can work directly on the byte array.
        URI uri = null;
        try {
            uri = new URI(new String(buffer.array(), buffer.position(), uriLength, ENCODING_CHARSET));
        } catch (Exception e) {
            LOGGER.error("Couldn't parse URI. Returning null.", e);
            return null;
        }
        buffer.position(buffer.position() + uriLength);
        // If there is an IP address
        int ipAddressLength = buffer.get();
        InetAddress ipAddress = null;
        if (ipAddressLength > 0) {
            int newPos = buffer.position() + ipAddressLength;
            try {
                ipAddress = InetAddress.getByAddress(Arrays.copyOfRange(buffer.array(), buffer.position(), newPos));
            } catch (UnknownHostException e) {
                LOGGER.error("Couldn't parse IP address. Returning null.", e);
                return null;
            }
            buffer.position(newPos);
        }
        return new CrawleableUri(uri, ipAddress, UriType.values()[typeId]);
    }


    /**
     * Constructor.
     * 
     * @param uri
     *            the URI
     */
    public CrawleableUri(URI uri) {
        this(uri, null);
    }

    /**
     * Constructor.
     * 
     * @param uri
     *            the URI
     * @param ipAddress
     *            the IP of this URI
     */
    public CrawleableUri(URI uri, InetAddress ipAddress) {
        this.uri = uri;
        this.ipAddress = ipAddress;
    }

    @Deprecated
    public CrawleableUri(URI uri, InetAddress ipAddress, UriType type) {
        this.uri = uri;
        this.ipAddress = ipAddress;
        this.type = type;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Deprecated
    public UriType getType() {
        return type;
    }

    @Deprecated
    public void setType(UriType type) {
        this.type = type;
    }

    public URI getUri() {
        return uri;
    }

    /**
     * Adds the given data to this URI instance using the given key.
     * 
     * @param key
     *            the identifier of the given data
     * @param data
     *            the data that should be attached to this URI
     */
    public void addData(String key, Object data) {
        this.data.put(key, data);
    }

    /**
     * Returns the data that is attached to this URI with the given key or
     * {@code null} if it does not exist.
     * 
     * @param key
     *            the identifier of the data
     * @return the data that is attached to this URI with the given key or
     *         {@code null} if it does not exist
     */
    public Object getData(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return null;
        }
    }

    /**
     * Returns the complete data attached to this URI. Note that the IP of the URI
     * as well as the URI itself have to be retrieved using the other get methods.
     * 
     * @return the complete data attached to this URI
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Overrides the data attached to this URI by the given data.
     * 
     * @param data the data map that should be attached to this URI
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CrawleableUri other = (CrawleableUri) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }

    /**
     *
     * @return
     * @deprecated Use the JSON serialization instead.
     */
    @Deprecated
    public ByteBuffer toByteBuffer() {
        byte uriBytes[] = uri.toString().getBytes(ENCODING_CHARSET);
        int bytesLength = 6 + uriBytes.length;
        byte ipAddressBytes[] = null;
        if (ipAddress != null) {
            ipAddressBytes = ipAddress.getAddress();
            bytesLength += ipAddressBytes.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(bytesLength);
        buffer.put((byte) type.ordinal());
        buffer.putInt(uriBytes.length);
        buffer.put(uriBytes);
        if (ipAddressBytes != null) {
            buffer.put((byte) ipAddressBytes.length);
            buffer.put(ipAddressBytes);
        } else {
            buffer.put((byte) 0);
        }
        return buffer;
    }

    /**
     *
     * @return
     * @deprecated Use the JSON serialization instead.
     */
    @Deprecated
    public byte[] toByteArray() {
        return toByteBuffer().array();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CrawleableUri(\"");
        builder.append(uri.toString());
        builder.append("\",");
        if (ipAddress != null) {
            builder.append(ipAddress.toString());
        }
        builder.append(',');
        builder.append(type.name());
        builder.append(')');
        return builder.toString();
    }

    public long getTimestampNextCrawl() {
        return timestampNextCrawl;
    }

    public void setTimestampNextCrawl(long timestampNextCrawl) {
        this.timestampNextCrawl = timestampNextCrawl;
    }
}
