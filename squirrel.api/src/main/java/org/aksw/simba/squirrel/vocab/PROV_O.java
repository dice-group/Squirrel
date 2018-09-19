package org.aksw.simba.squirrel.vocab;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class PROV_O {

    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = "http://www.w3.org/ns/prov#";

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
    
    public static final Resource Activity = resource("Activity");
    public static final Resource Association = property("Association");
    public static final Resource Plan = property("Plan");

    public static final Property agent = property("agent");
    public static final Property endedAtTime = property("endedAtTime");
    public static final Property hadPlan = property("hadPlan");
    public static final Property qualifiedAssociation = property("qualifiedAssociation");
    public static final Property startedAtTime = property("startedAtTime");
    public static final Property wasAssociatedWith = property("wasAssociatedWith");
    public static final Property wasGeneratedBy = property("wasGeneratedBy");

}
