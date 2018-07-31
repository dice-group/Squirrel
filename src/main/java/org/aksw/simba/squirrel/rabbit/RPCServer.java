package org.aksw.simba.squirrel.rabbit;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.DataHandler;
import org.hobbit.core.rabbit.DataReceiverImpl;
import org.hobbit.core.rabbit.RabbitQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class RPCServer extends DataReceiverImpl implements ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataReceiverImpl.class);

    protected Channel responseChannel;

    protected RPCServer(RabbitQueue queue, RespondingDataHandler handler, int maxParallelProcessedMsgs,
            Channel responseChannel) throws IOException {
        super(queue, handler, maxParallelProcessedMsgs);
        this.responseChannel = responseChannel;
    }

    /**
     * Returns a newly created {@link Builder}.
     * 
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void sendResponse(byte[] data, String responseQueueName, String correlId) {
        try {
            BasicProperties props = (new BasicProperties.Builder()).correlationId(correlId).build();
            responseChannel.basicPublish("", responseQueueName, props, data);
        } catch (Exception e) {
            LOGGER.error("Exception while sending response.", e);
        }
    }

    @Override
    protected Runnable buildMsgProcessingTask(Delivery delivery) {
        return new MsgProcessingTask(delivery, this);
    }

    protected class MsgProcessingTask implements Runnable {

        private Delivery delivery;
        private ResponseHandler responseHandler;

        public MsgProcessingTask(Delivery delivery, ResponseHandler responseHandler) {
            this.delivery = delivery;
            this.responseHandler = responseHandler;
        }

        @Override
        public void run() {
            try {
                String replyTo = delivery.getProperties().getReplyTo();
                String correlationId = delivery.getProperties().getCorrelationId();
                if ((replyTo != null) && (correlationId != null)) {
                    ((RespondingDataHandler) getDataHandler()).handleData(delivery.getBody(), responseHandler, replyTo,
                            correlationId);
                } else {
                    getDataHandler().handleData(delivery.getBody());
                }
            } catch (Throwable e) {
                LOGGER.error("Uncatched throwable when processing an incoming message in the RPCServer.", e);
            }
        }

    }

    public static class Builder extends DataReceiverImpl.Builder {

        private RabbitQueueFactory responseFactory = null;

        public Builder() {
        };

        /**
         * Sets the handler that is called if data is incoming. <b>Note</b> that a
         * {@link RespondingDataHandler} is expected.
         * 
         * @param dataHandler
         *            the RespondingDataHandler instance that is called if data is
         *            incoming
         * @return this builder instance
         */
        public Builder dataHandler(DataHandler dataHandler) {
            if (dataHandler instanceof RespondingDataHandler) {
                this.dataHandler = dataHandler;
                return this;
            } else {
                throw new IllegalArgumentException(
                        "An instance of " + RespondingDataHandler.class.getSimpleName() + " has been expected.");
            }
        }

        /**
         * Method for providing the necessary information to connect to the queue to
         * which responses should be sent. Note that if this information is not
         * provided, the {@link RPCServer} instance is still working but might have
         * performance problems or might get stuck.
         * 
         * @param factory
         *            the queue factory used to connect to
         * @return this builder instance
         */
        public Builder responseQueueFactory(RabbitQueueFactory factory) {
            this.responseFactory = factory;
            return this;
        }

        /**
         * Sets the maximum number of incoming messages that are processed in parallel.
         * Additional messages have to wait in the queue.
         * 
         * @param maxParallelProcessedMsgs
         *            the maximum number of incoming messages that are processed in
         *            parallel
         * @return this builder instance
         */
        public Builder maxParallelProcessedMsgs(int maxParallelProcessedMsgs) {
            this.maxParallelProcessedMsgs = maxParallelProcessedMsgs;
            return this;
        }

        /**
         * Builds the {@link DataReceiverImpl} instance with the previously given
         * information.
         * 
         * @return The newly created DataReceiver instance
         * @throws IllegalStateException
         *             if the dataHandler is missing or if neither a queue nor the
         *             information needed to create a queue have been provided.
         * @throws IOException
         *             if an exception is thrown while creating a new queue or if the
         *             given queue can not be configured by the newly created
         *             DataReceiver. <b>Note</b> that in the latter case the queue will
         *             be closed.
         */
        public DataReceiverImpl build() throws IllegalStateException, IOException {
            if (dataHandler == null) {
                throw new IllegalStateException(DATA_HANDLER_MISSING_ERROR);
            }
            if (queue == null) {
                if ((queueName == null) || (factory == null)) {
                    throw new IllegalStateException(QUEUE_INFO_MISSING_ERROR);
                } else {
                    // create a new queue
                    queue = factory.createDefaultRabbitQueue(queueName);
                }
            }
            Channel responseChannel;
            if (responseFactory == null) {
                if (factory == null) {
                    responseChannel = queue.getChannel();
                } else {
                    responseChannel = factory.createChannel();
                }
            } else {
                responseChannel = responseFactory.createChannel();
            }
            try {
                return new RPCServer(queue, (RespondingDataHandler) dataHandler, maxParallelProcessedMsgs,
                        responseChannel);
            } catch (IOException e) {
                IOUtils.closeQuietly(queue);
                throw e;
            }
        }
    }

}
