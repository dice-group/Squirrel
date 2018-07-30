package org.aksw.simba.squirrel.sink;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A sink that can handle unstructured data.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
@Component
public interface UnstructuredDataSink extends SinkBase {

    /**
     * Stores the given data for the given URI.
     *
     * @param uri
     *            the URI for which the data should be stored
     * @param data
     *            the data that should be stored
     */
    public default void addData(CrawleableUri uri, String data) {
        addData(uri, data.getBytes(Constants.DEFAULT_CHARSET));
    }

    /**
     * Stores the given data for the given URI.
     *
     * @param uri
     *            the URI for which the data should be stored
     * @param data
     *            the data that should be stored
     */
    public default void addData(CrawleableUri uri, byte data[]) {
        addData(uri, new ByteArrayInputStream(data));
    }

    /**
     * Stores the data from the given stream for the given URI.
     *
     * @param uri
     *            the URI for which the data should be stored
     * @param stream
     *            the stream from which the data will be read that should be stored
     */
    public void addData(CrawleableUri uri, InputStream stream);

}
