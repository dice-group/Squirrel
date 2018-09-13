package org.aksw.simba.squirrel.vocab;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class DCTerms {

    /**
     * The namespace of the vocabulary as a string
     */
    public static final String uri = "http://purl.org/dc/terms/";

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

    public static final Property accessRights = property("accessRights");
    public static final Property accrualPeriodicity = property("accrualPeriodicity");
    public static final Property conformsTo = property("conformsTo");
    public static final Property description = property("description");
    public static final Property hasVersion = property("hasVersion");
    public static final Property identifier = property("identifier");
    public static final Property issued = property("issued");
    public static final Property isVersionOf = property("isVersionOf");
    public static final Property language = property("language");
    public static final Property license = property("license");
    public static final Property modified = property("modified");
    public static final Property provenance = property("provenance");
    public static final Property publisher = property("publisher");
    public static final Property rights = property("rights");
    public static final Property source = property("source");
    public static final Property spatial = property("spatial");
    public static final Property temporal = property("temporal");
    public static final Property title = property("title");
    public static final Property type = property("type");

}
