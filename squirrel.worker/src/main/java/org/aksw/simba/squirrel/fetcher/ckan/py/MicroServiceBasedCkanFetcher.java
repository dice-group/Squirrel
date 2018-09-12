package org.aksw.simba.squirrel.fetcher.ckan.py;

import java.io.File;
import java.io.IOException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.fetcher.Fetcher;

import com.rabbitmq.client.*;

/**
 * A micro service client which communicates via RabbitMQ and assumes that the
 * micro service is able to fetch the content of a CKAN.
 *
 * @author Varun Maitreya Eranki
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 */

public class MicroServiceBasedCkanFetcher implements Fetcher {

    // Sends a message to python end via rabbitMQueue CKAN
    public static String send(String s) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // Declare SENDQueue and convert String to Bytes and sends it to CKAN queue
        channel.queueDeclare("ckan", false, false, false, null);
        channel.basicPublish("", "ckan", null, s.getBytes("UTF-8"));
        // console output for testing
        System.out.println(" [x] Sent '" + s + "'");
        channel.close();
        connection.close();

        return "code 0";
    }

    // Receives a message from python end via rabbitMQueue CKAN2
    public static String recieve() throws Exception {
        String bytes = new String("empty");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // Declare RECIEVEQueue named CKAN2
        channel.queueDeclare("ckan2", false, false, false, null);
        // Consumer consumes all messages continuously
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                // TODO: FIND A SPECIFIC METHOD IN RABBITMQ TO HANDLE RETURNING MESSAGES
                // return message;
            }
        };
        try {
            // start a consumer for CKAN2 queue
            channel.basicConsume("ckan2", true, consumer);
            // TODO:MESSAGE FROM handleDelivery SHOULD BE RECEIVED AND RETURNED TO
            // WORKERIMPL
        } catch (IOException e) {
        }
        return bytes;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public File fetch(CrawleableUri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    // THIS CODE BLOCK CAN BE USED TO CHECK WORKING MECHANISM
    /*
     * public static void main(String[] argv) throws Exception {
     * 
     * String url = "https://demo.ckan.org" ; String s = CkanComponent.send(url);
     * System.out.println(s); String s1 = CkanComponent.recieve();
     * //ArrayList<String> s1 = CkanComponent.recieve(); System.out.println(s1);
     * 
     * }
     */

    // THIS BLOCK REPRESENTS A CONTINUOUS MESSAGE MECHANISM DESIGNED FOR
    // COMMUNICATING WITH PYTHON CKAN END.
    // THIS IS NO LONGER USED AND NOT A RELIABLE SERVICE DUE TO LARGE OVER HEAD OF
    // MESSAGES.
    /*
     * public static void main(String[] argv) throws Exception {
     * 
     * ConnectionFactory factory = new ConnectionFactory();
     * factory.setHost("localhost"); Connection connection =
     * factory.newConnection(); Channel channel = connection.createChannel();
     * 
     * channel.queueDeclare("ckan", false, false, false, null);
     * channel.queueDeclare("ckan2", false, false, false, null);
     * 
     * Consumer consumer = new DefaultConsumer(channel) {
     * 
     * @Override public void handleDelivery(String consumerTag, Envelope envelope,
     * AMQP.BasicProperties properties, byte[] body) throws IOException { String
     * message = new String(body, "UTF-8"); System.out.println(" [x] Received '" +
     * message + "'"); if(message.isEmpty()){ String message1 = "hi ckan"; //String
     * message = "https://demo.ckan.org"; channel.basicPublish("", "ckan", null,
     * message1.getBytes("UTF-8")); System.out.println(" [x] Sent '" + message1 +
     * "'"); } else if (message.equals("hello component")) { String message2 =
     * "https://www.europeandataportal.eu/"; //String message2 =
     * "https://demo.ckan.org"; channel.basicPublish("", "ckan", null,
     * message2.getBytes("UTF-8")); System.out.println(" [x] Sent '" + message2 +
     * "'"); } else if (message.equals("finished dumping")) {
     * 
     * }
     * 
     * } };
     * 
     * channel.basicConsume("ckan2", true, consumer);
     * 
     * }
     */

}
