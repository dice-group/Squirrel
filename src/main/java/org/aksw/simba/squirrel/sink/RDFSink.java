package org.aksw.simba.squirrel.sink;

import com.rabbitmq.client.ConnectionFactory;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class RDFSink implements Sink {

    private static String strContentDatasetUriUpdate;
    private static final Logger LOGGER = LoggerFactory.getLogger(RDFSink.class);
    private static String strMetaDatasetUriUpdate;

    public RDFSink() {
        String strIP = null;
        try {
            strIP = InetAddress.getLocalHost().getHostAddress().toString();
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("jena");
            factory.setUsername("admin");
            factory.setPassword("pw123");
            factory.setPort(3030);
            LOGGER.info("ip of jena :" + factory.getSocketFactory());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        // TODO: find out ip address of triple store container at runtime
        strIP = "192.168.0.136";
        strContentDatasetUriUpdate = "http://" + strIP + ":3030/ContentSet/update";
        strMetaDatasetUriUpdate = "http://" + strIP + ":3030/MetaData/update";
    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        List<Triple> lstTriples = new ArrayList<>();
        Node nodeSubject = new Node_Variable("crawlingActivity" + crawlingActivity.getId());
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("date_started"), new Node_Variable(crawlingActivity.getDateStarted().toString())));
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("date_ended"), new Node_Variable(crawlingActivity.getDateEnded().toString())));
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("state"), new Node_Variable(crawlingActivity.getStatus().toString())));
        lstTriples.add(new Triple(nodeSubject, new Node_Variable("id_of_worker"), new Node_Variable(String.valueOf(crawlingActivity.getWorker().getId()))));

        lstTriples.forEach(triple -> {
            UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(String.valueOf(crawlingActivity.getId()), triple));
            UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, strMetaDatasetUriUpdate);
            proc.execute();
        });

    }

    // this is only for testing
    public static void main(String[] argv) {
        try {
            InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        RDFSink sink = new RDFSink();
        //System.out.println(strContentDatasetUriUpdate);
        //CrawleableUri uri=  new CrawleableUriFactoryImpl().create("http://www.testPage.de");
        CrawleableUri uri = new CrawleableUriFactoryImpl().create("http://www.google.de");
        Node node = new Node_Variable("subj1");
        Node node2 = new Node_Variable("pred1");
        Node node3 = new Node_Variable("obj1");
        Triple triple1 = new Triple(node, node2, node3);

        sink.addTriple(uri, triple1);

    }

    @Override
    public void addTriple(CrawleableUri uri, Triple triple) {
        LOGGER.error("addTripple");
        //todo: here you can recoginze that another triple has been stored for this given uri

        //Get the graphID for the uri - may change to Hashvalue
        //String graphUri = uri.toString();

        //May check if triple already exists
        //e.g. with select query (Limit=1)
        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(uri, triple));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, strContentDatasetUriUpdate);
        proc.execute();
    }

    @Override
    public void openSinkForUri(CrawleableUri uri) {
//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        Credentials credentials = new UsernamePasswordCredentials("admin", "pw123");
//        credsProvider.setCredentials(AuthScope.ANY, credentials);
//        HttpClient httpclient = HttpClients.custom()
//            .setDefaultCredentialsProvider(credsProvider)
//            .build();
//        HttpOp.setDefaultHttpClient(httpclient);

    }

    @Override
    public void closeSinkForUri(CrawleableUri uri) {

    }

    @Override
    public void addData(CrawleableUri uri, InputStream stream) {

    }
}
