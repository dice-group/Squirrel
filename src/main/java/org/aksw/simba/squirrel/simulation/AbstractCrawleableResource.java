package org.aksw.simba.squirrel.simulation;

import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;

/**
 * This abstract class contains methods for a {@link CrawleableResource} that is
 * based on an RDF model.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public abstract class AbstractCrawleableResource implements CrawleableResource {

    /**
     * Name of the resource.
     */
    private final String resourceName;
    /**
     * Content type of the resource.
     */
    private final String contentType;
    /**
     * Data of the resource.
     */
    private final Model model;

    /**
     * Constructor.
     * 
     * @param model
     *            the RDF data of the resource
     * @param resourceName
     *            the name of the resource
     * @param contentType
     *            content type of the resource
     */
    public AbstractCrawleableResource(Model model, String resourceName, String contentType) {
        this.resourceName = resourceName;
        this.contentType = contentType;
        this.model = model;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public void writeResourceData(OutputStream out) {
        serializeModel(out, model);
    }

    /**
     * This method writes the given RDF model to the given output stream using
     * the predefined {@link #contentType}.
     * 
     * @param out
     *            output stream to which the data should be written
     * @param model
     *            data that should be written
     */
    protected abstract void serializeModel(OutputStream out, Model model);

    @Override
    public String getResourceContentType() {
        return contentType;
    }

    @Override
    public Model getModel() {
        return model;
    }

}
