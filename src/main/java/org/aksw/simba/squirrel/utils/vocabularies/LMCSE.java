package org.aksw.simba.squirrel.utils.vocabularies;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;


/**
 * Vocabulary defined in the scope of mCloud data retrieval,
 * containing required Properties and Resources that were not sufficiently defined in other vocabularies 
 * LMCSE is an abbreviation for LimboMCloudStatisticsEngine
 */
public class LMCSE
{
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();

    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://limbo-mCloudStatisticsEngine.org/metadata#";

    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI()
    {
        return NS;
    }

    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource(NS);

    /** <p>The ontology's owl:versionInfo as a string</p> */
    public static final String VERSION_INFO = "1.0";

    /** <p>indicates which protocol/API/Webservice can be used to download data</p> */
    public static final Property accessType = M_MODEL.createProperty(NS + "accessType");

    /** 
     * <p>Placeholder to collect all Datasets that have no license attached or errors retrieving the license</p>
     */
    public static final String NullLicense = NS + "NullLicense";

    /**
     *  <p>Placeholder to collect all Datasets that have no publisher attached or errors retrieving the publisher</p>
     */
    public static final String NullPublisher = NS + "NullPublisher";
    
    /**
     *  <p>Placeholder to collect all Distributions for which the accessType of the URI could not be parsed</p>
     */
    public static final String NullAccessType = NS + "NullAccessType";
}
