package org.dice_research.squirrel.rabbit;

import org.hobbit.core.rabbit.DataHandler;

public interface RespondingDataHandler extends DataHandler {

    void handleData(byte data[], ResponseHandler handler, String responseQueueName, String correlId);
}
