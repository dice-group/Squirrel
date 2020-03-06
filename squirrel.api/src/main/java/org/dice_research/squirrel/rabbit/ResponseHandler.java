package org.dice_research.squirrel.rabbit;

/**
 * An interface for a class which is able to send a response based on the given
 * data.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface ResponseHandler {

    /**
     * Sends the response of a received request using the given data and meta
     * information.
     * 
     * @param data
     *            the data that should be sent as response
     * @param responseQueueName
     *            the name of the queue which is used for the response
     * @param correlId
     *            the correlation ID which is needed by the receiver of the response
     *            to map it to the request that is answered with this data
     */
    public void sendResponse(byte[] data, String responseQueueName, String correlId);
}
