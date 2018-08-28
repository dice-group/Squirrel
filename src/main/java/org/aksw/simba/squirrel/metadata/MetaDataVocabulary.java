package org.aksw.simba.squirrel.metadata;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * This class contains all necessary resources and propertys for the metadata
 */
public class MetaDataVocabulary {
    public static final String baseProv = "http://www.w3.org/ns/prov-o#";
    public static final String baseSq = "http://w3id.org/squirrel/vocab#";
    public static final String baseMeta = "http://w3id.org/squirrel/meta#";

    public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";

    private static final Model model = ModelFactory.createDefaultModel();
    
    public static final Resource NAMESPACE = model.createResource(baseProv);
    public static final Resource WORKERASSOCIATION = model.createResource(baseMeta + "workerAssociation");



    public static final Property startedAtTime = model.createProperty(baseProv + "startedAtTime");
    public static final Property endedAtTime = model.createProperty(baseProv + "endedAtTime");
    public static final Property wasAssociatedWith = model.createProperty(baseProv + "wasAssociatedWith");
    public static final Property wasGeneratedBy = model.createProperty(baseProv + "wasGeneratedBy");


    public static final Property status = model.createProperty(baseSq + "status");
    public static final Property numberOfTriples = model.createProperty(baseSq + "numberOfTriples");
    public static final Property hostedOn = model.createProperty(baseSq + "hostedOn");
    public static final Property uriName = model.createProperty(baseSq + "uriName");

    public static final Property rdfs_comment = model.createProperty(rdfs + "comment");
    
    public static final Property agent = model.createProperty(baseProv + "agent");
    public static final Property qualifiedAssociation = model.createProperty(baseProv + "qualifiedAssociation");
    public static final Property Association = model.createProperty(baseProv + "Association");
    public static final Property hadPlan = model.createProperty(baseProv + "hadPlan");
}