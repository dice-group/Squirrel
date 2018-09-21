package org.dice_research.squirrel.rabbit;

public interface ResponseHandler {

    public void sendResponse(byte[] data, String responseQueueName, String correlId);
}
