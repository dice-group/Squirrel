package org.dice_research.squirrel.simulation;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple string-based resource that returns the given String.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SimpleStringResource implements CrawleableResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleStringResource.class);
    
    private String resourceName;
    private String resource;

    /**
     * Constructor.
     * 
     * @param resourceName
     *            the name of the resource
     * @param resource
     *            the string that is returned when triggered
     */
    public SimpleStringResource(String resourceName, String resource) {
        this.resourceName = resourceName;
        this.resource = resource;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public void writeResourceData(OutputStream out) {
        try {
            out.write(resource.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error("Exception while writing resource data.", e);
        }
    }

    @Override
    public String getResourceContentType() {
        return "text/*";
    }

    @Override
    public Model getModel() {
        return null;
    }

}
