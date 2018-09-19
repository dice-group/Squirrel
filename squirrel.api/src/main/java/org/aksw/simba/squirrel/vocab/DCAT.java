package org.aksw.simba.squirrel.vocab;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class DCAT {

    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = "http://www.w3.org/ns/dcat#";

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
    
    public static final Resource Dataset = resource("Dataset");
    public static final Resource Distribution = property("Distribution");

    public static final Property accessURL = property("accessURL");
    public static final Property byteSize = property("byteSize");
    public static final Property contactPoint = property("contactPoint");
    public static final Property downloadURL = property("downloadURL");
    public static final Property distribution = property("distribution");
    public static final Property keyword = property("keyword");
    public static final Property landingPage = property("landingPage");
    public static final Property mediaType = property("mediaType");
    public static final Property theme = property("theme");
}
