package org.aksw.simba.squirrel.simulation;

import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;

public interface CrawleableResource {

    public String getResourceName();

    public void writeResourceData(OutputStream out);

    public String getResourceContentType();
    
    public Model getModel();
}
