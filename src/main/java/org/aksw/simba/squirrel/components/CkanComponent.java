package org.aksw.simba.squirrel.components;

import com.rabbitmq.client.*;

import java.io.IOException;




public class CkanComponent {


    //private final static String QUEUE_NAME = "ckan";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("ckan", false, false, false, null);
        String message = "hi ckan";
        //String message = "https://demo.ckan.org";
        channel.basicPublish("", "ckan", null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");

        channel.queueDeclare("ckan2", false, false, false, null);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                if(message.equals("hello component")){
                    String message2 = "https://demo.ckan.org";
                    channel.basicPublish("", "ckan", null, message2.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + message2 + "'");
                }
//                else
//                    if(message.equals("finished dumping")){
//                    channel.close();
//                    connection.close();
//                }

            }
        };
        channel.basicConsume("ckan2", true, consumer);


        //channel.close();
        //connection.close();
    }
}


