package org.dice_research.squirrel.vocab;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.squirrel.Constants;

public class Squirrel {

    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = Constants.SQUIRREL_URI_PREFIX + "/vocab#";
    
    public static final Resource ResultGraph = resource("ResultGraph");
    public static final Resource ResultFile = resource("ResultFile");

    public static final Property approxNumberOfTriples = property("approxNumberOfTriples");
    public static final Property depth = property("depth");
    public static final Property crawled = property("crawled");
    public static final Property uriHostedOn = property("uriHostedOn");
    public static final Property status = property("status");
    public static final Property containsDataOf = property("containsDataOf");

    /**
     * returns the URI for this schema
     * 
     * @return the URI for this schema
     */
    public static String getURI() {
        return uri;
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }

   
    
}
