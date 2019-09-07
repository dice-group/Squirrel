package org.dice_research.squirrel.rabbit;

import org.hobbit.core.rabbit.DataHandler;

/**
 * Interface of a {@link DataHandler} which is able to respond to the message
 * using the given {@link ResponseHandler}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public interface RespondingDataHandler extends DataHandler {

    /**
     * Extendsion of {@link DataHandler#handleData(byte[])} which takes a
     * {@link ResponseHandler} and the header information necessary to send the
     * response to the given data.
     * 
     * @param data
     *            the data received in the requesting message
     * @param handler
     *            {@link ResponseHandler} used to send the response
     * @param responseQueueName
     *            the response queue name
     * @param correlId
     *            the correlation ID necessary for the original message sender to
     *            map the response to the request
     */
    public void handleData(byte data[], ResponseHandler handler, String responseQueueName, String correlId);
}
