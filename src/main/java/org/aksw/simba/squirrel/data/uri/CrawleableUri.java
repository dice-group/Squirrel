package org.aksw.simba.squirrel.data.uri;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a URI and additional meta data that is helpful for
 * crawling it.
 * 
 * <p>
 * <b>Serialization</b> - objects of this class can be serialized to byte
 * arrays. These arrays are organized as follows.<br>
 * <code>bytes[0] = </code>ordinal number of the {@link #type}, we use
 * {@link UriType#UNKNOWN} if the attribute is null.<br>
 * <code>bytes[1 to 4] = </code>length <code>uLength</code>of the URI in bytes.
 * <br>
 * <code>bytes[{@value #URI_START_INDEX} to (uLength+4)] = </code> URI in bytes
 * with {@link #CHARSET_NAME}={@value #CHARSET_NAME} as charset.<br>
 * If <code>(bytes.length > (uLength + {@value #URI_START_INDEX}))</code> then
 * the remaining bytes are the {@link #ipAddress}.
 * </p>
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class CrawleableUri {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawleableUri.class);

    private static final String CHARSET_NAME = "UTF-8";
    private static final Charset ENCODING_CHARSET = Charset.forName(CHARSET_NAME);
    private static final int URI_START_INDEX = 5;

    /**
     * Creates a CrawleableUri object from the given byte array.
     * 
     * @param bytes
     * @return
     */
    public static CrawleableUri fromByteArray(byte bytes[]) {
        if ((bytes == null) || (bytes.length < URI_START_INDEX)) {
            return null;
        }
        // We need the buffer to get int values from the byte array.
        return fromByteBuffer(ByteBuffer.wrap(bytes));
    }

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

    private final URI uri;
    private InetAddress ipAddress;
    private UriType type;

    public CrawleableUri(URI uri) {
        this(uri, null, UriType.UNKNOWN);
    }

    public CrawleableUri(URI uri, InetAddress ipAddress) {
        this(uri, ipAddress, UriType.UNKNOWN);
    }

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

    public UriType getType() {
        return type;
    }

    public void setType(UriType type) {
        this.type = type;
    }

    public URI getUri() {
        return uri;
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
}
