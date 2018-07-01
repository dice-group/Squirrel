package org.aksw.simba.squirrel.components;

import com.rabbitmq.client.*;

import java.io.IOException;


public class CkanComponent {

    private final static String QUEUE_NAME = "ckan";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "hi ckan";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");

        //TODO:MODIFY FOR RECEIVING SPECIFIC MESSAGE INSTEAD OF EXITING AFTER SENDING MESSAGE
//        Consumer consumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
//                throws IOException {
//                String message = new String(body, "UTF-8");
//                System.out.println(" [x] Received '" + message + "'");
//            }
//        };
//        channel.basicConsume(QUEUE_NAME, true, consumer);


        channel.close();
        connection.close();
    }
}


