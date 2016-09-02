package org.aksw.simba.squirrel.simulation;

import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractCrawleableResource implements CrawleableResource {

    private final String resourceName;
    private final String contentType;
    private final Model model;

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
