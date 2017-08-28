package org.aksw.simba.squirrel.simulation;

import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;

/**
 * This interface defines the methods of a resource that is hosted in a
 * {@link CrawleableResourceContainer} and can be used to simulate a resource
 * that can be crawled.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface CrawleableResource {

    /**
     * The name (path of the URI) of the resource.
     * 
     * @return the name of the resource
     */
    public String getResourceName();

    /**
     * Writes the data of the resource to the given output stream.
     * 
     * @param out
     *            stream to which the data of the resource should be written to.
     */
    public void writeResourceData(OutputStream out);

    /**
     * Content type of the resource.
     * 
     * @return Content type of the resource
     */
    public String getResourceContentType();

    /**
     * RDF data of the resource that can be used for a comparison between the
     * expected and the crawled data.
     * 
     * @return RDF data of the resource
     */
    public Model getModel();
}
