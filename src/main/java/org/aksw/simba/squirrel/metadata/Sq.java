package org.aksw.simba.squirrel.metadata;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class Sq {
    public static final String NS = "https://www.w3id.org/squirrel/vocab/";

    public static String getURI() {
        return NS;
    }

    private static final Model m_model = ModelFactory.createDefaultModel();
    public static final Resource NAMESPACE = m_model.createResource(NS);

    public static final Property status = m_model.createProperty("https://www.w3id.org/squirrel/vocab/status");
    public static final Property numberOfTriples = m_model.createProperty("https://www.w3id.org/squirrel/vocab/numberOfTriples");
    public static final Property hostedOn= m_model.createProperty("https://www.w3id.org/squirrel/vocab/hostedOn");
    public static final Property OfUri = m_model.createProperty("https://www.w3id.org/squirrel/vocab/uriName");
    public static final Property steps = m_model.createProperty("https://www.w3id.org/squirrel/vocab/steps");

}
