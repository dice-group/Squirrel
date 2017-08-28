package org.aksw.simba.squirrel.components;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.simba.squirrel.simulation.CrawleableResource;
import org.aksw.simba.squirrel.simulation.CrawleableResourceContainer;
import org.aksw.simba.squirrel.simulation.DumpResource;
import org.aksw.simba.squirrel.simulation.StringResource;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.components.Component;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpServerComponent implements Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpServerComponent.class);

    public static final String MODEL_KEY = "RESOURCE_MODEL";
    public static final String MODEL_LANG_KEY = "RESOURCE_MODEL_LANG";
    public static final String SERVER_PORT_KEY = "PORT";
    public static final String DUMP_FILE_NAME_KEY = "DUMP_FILE_NAME";
    public static final String USE_DEREF_KEY = "USE_DEREF";

    protected Container container;
    protected Server server;
    protected Connection connection;

    @Override
    public void init() throws Exception {
        Map<String, String> env = System.getenv();
        String modelFile = null;
        if (env.containsKey(MODEL_KEY)) {
            modelFile = env.get(MODEL_KEY);
        } else {
            throw new IllegalArgumentException("Couldn't get " + MODEL_KEY + " from the environment.");
        }
        String modelLang = null;
        if (env.containsKey(MODEL_LANG_KEY)) {
            modelLang = env.get(MODEL_LANG_KEY);
        } else {
            throw new IllegalArgumentException("Couldn't get " + MODEL_LANG_KEY + " from the environment.");
        }
        int port = 0;
        if (env.containsKey(SERVER_PORT_KEY)) {
            try {
                port = Integer.parseInt(env.get(SERVER_PORT_KEY));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Couldn't parse the value of " + SERVER_PORT_KEY + ".", e);
            }
        } else {
            throw new IllegalArgumentException("Couldn't get " + SERVER_PORT_KEY + " from the environment.");
        }
        String dumpFileName = null;
        if (env.containsKey(DUMP_FILE_NAME_KEY)) {
            dumpFileName = env.get(DUMP_FILE_NAME_KEY);
        } else {
            LOGGER.info("There is no value for " + DUMP_FILE_NAME_KEY + ". There won't be a dump file on this server.");
        }
        boolean useDeref = true;
        if (env.containsKey(USE_DEREF_KEY)) {
            try {
                useDeref = Boolean.parseBoolean(env.get(USE_DEREF_KEY));
            } catch (NumberFormatException e) {
                LOGGER.warn("Couldn't parse the value of " + USE_DEREF_KEY + ". Will set it to " + useDeref + ".", e);
            }
        }

        Model model = readModel(modelFile, modelLang);
        if (model == null) {
            throw new IllegalArgumentException("Couldn't read model file.");
        }
        List<CrawleableResource> resources = new ArrayList<>();
        if (dumpFileName != null) {
            resources.add(new DumpResource(model, dumpFileName, Lang.N3));
        }
        if (useDeref) {
            addDeref(resources, model);
        }

        container = new CrawleableResourceContainer(resources.toArray(new CrawleableResource[resources.size()]));
        server = new ContainerServer(container);
        connection = new SocketConnection(server);
        SocketAddress address = new InetSocketAddress(port);
        connection.connect(address);

        LOGGER.info("HTTP server initialized.");
    }

    protected Model readModel(String modelFile, String modelLang) {
        Model model = ModelFactory.createDefaultModel();
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(modelFile);
            model.read(fin, "", modelLang);
        } catch (Exception e) {
            LOGGER.error("Couldn't read model file. Returning null.", e);
            return null;
        } finally {
            IOUtils.closeQuietly(fin);
        }
        return model;
    }

    /**
     * Adds a dereferencing resource for every subject that is available in the
     * given model.
     * 
     * @param resources
     *            the list of crawleable resources this server is using
     * @param model
     *            the model from which the subjects and the data that will be
     *            returned when they are dereferenced is collected
     */
    protected void addDeref(List<CrawleableResource> resources, Model model) {
        ResIterator iterator = model.listSubjects();
        Resource subject;
        Model resourceModel;
        while (iterator.hasNext()) {
            subject = iterator.next();
            resourceModel = ModelFactory.createDefaultModel();
            resourceModel.add(model.listStatements(subject, null, (RDFNode) null));
            resources.add(new StringResource(resourceModel, subject.getURI(), Lang.N3));
        }
    }

    @Override
    public void run() throws Exception {
        synchronized (this) {
            this.wait();
        }
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(connection);
        if (server != null) {
            server.stop();
        }
    }
    
}
