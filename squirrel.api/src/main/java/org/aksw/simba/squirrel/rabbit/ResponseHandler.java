package org.aksw.simba.squirrel.rabbit;

public interface ResponseHandler {

    public void sendResponse(byte[] data, String responseQueueName, String correlId);
}
