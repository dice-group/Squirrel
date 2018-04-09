package org.aksw.simba.squirrel.utils.vocabularies;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;


public class LMCSE
{
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model M_MODEL = ModelFactory.createDefaultModel();

    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://limbo-mCloudStatisticsEngine.org/dcat#";

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

    //Resources and Properties used by Squirrel to store metadata model
    public static final Property mCloudResourceUri = M_MODEL.createProperty(NS + "mCloudResource");

    /** <p>indicates which protocol/API can be used to download data</p> */
    public static final Property accessType = M_MODEL.createProperty(NS + "accessType");

    /** <p>As long as triple stores are not available in Squirrel we store the data as files. 
     *     To link the metadata dataset to the actual dataset we reference the filename that is constructed from the datasets URI.
     *     Does not represent wheter compression is used or not</p> */
    public static final Property fileBasedLink = M_MODEL.createProperty(NS + "fileBasedLink");

    /** <p>Placeholder to collect all Datasets that have no category attached</p> */
    public static final String NullCategory = NS + "NullCategory";

    /** <p>Placeholder to collect all Datasets that have no license attached or errors 
     *  retrieving the license</p>
     */
    public static final String NullLicense = NS + "NullLicense";

    /** <p>Placeholder to collect all Datasets that have no publisher attached or errors 
     *  retrieving the publisher</p>
     */
    public static final String NullPublisher = NS + "NullPublisher";

}
