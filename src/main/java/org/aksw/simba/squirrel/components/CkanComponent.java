package org.aksw.simba.squirrel.components;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CkanComponent {

    public static void main(String[] argv) throws Exception {

      ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("ckan", false, false, false, null);
        channel.queueDeclare("ckan2", false, false, false, null);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                if(message.isEmpty()){
                    String message1 = "hi ckan";
                    //String message = "https://demo.ckan.org";
                    channel.basicPublish("", "ckan", null, message1.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + message1 + "'");
                }
                else if (message.equals("hello component")) {
                    String message2 = "https://www.europeandataportal.eu/";
                    //String message2 = "https://demo.ckan.org";
                    channel.basicPublish("", "ckan", null, message2.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + message2 + "'");
                } else if (message.equals("finished dumping")) {

                }

            }
        };

        channel.basicConsume("ckan2", true, consumer);

    }
//    public List<String> recieve() throws Exception{
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//        List<String> list = new ArrayList<String>();
//        channel.queueDeclare("ckan2", false, false, false, null);
//        Consumer consumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
//                throws IOException {
//                String message = new String(body, "UTF-8");
//                System.out.println(" [x] Received '" + message + "'");
//            }
//        };
//        try {
//            byte[] bytes = channel.basicConsume("ckan2", true, consumer).getBytes();
//            System.out.println(bytes);
//            String s = bytes.toString();
//        }
//        catch (IOException e) {
//        }
//        return list;
//    }
//
//    public static String send(String s) throws Exception{
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//        channel.queueDeclare("ckan", false, false, false, null);
//        channel.basicPublish("", "ckan", null, s.getBytes("UTF-8"));
//        System.out.println(" [x] Sent '" + s + "'");
//        channel.close();
//        connection.close();
//
//        return "0";
//    }
//
//    public void main(String[] argv) throws Exception {
//
//            String url = "abc" ;
//            String s = CkanComponent.send(url);
//            String s1 = CkanComponent.recieve();
//
//
//        }
//
//
//    }
}


