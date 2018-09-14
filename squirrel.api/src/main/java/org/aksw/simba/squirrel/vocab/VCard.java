package org.aksw.simba.squirrel.vocab;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class VCard {

    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = "https://www.w3.org/2006/vcard/ns#";

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
    
    public static final Resource Kind = resource("Kind");

    public static final Property fn = property("fn");
    public static final Property hasEmail = property("hasEmail");
}
