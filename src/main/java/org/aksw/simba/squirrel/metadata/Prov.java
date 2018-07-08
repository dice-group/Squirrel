package org.aksw.simba.squirrel.metadata;
import org.apache.jena.rdf.model.*;

public class Prov
{


        public static final String NS = "http://www.w3.org/ns/prov/";

        public static String getURI() {
            return NS;
        }

        private static final Model m_model = ModelFactory.createDefaultModel();
        public static final Resource NAMESPACE = m_model.createResource(NS);


        public static final Property startedAtTime = m_model.createProperty("http://www.w3.org/ns/prov/startedAtTime");
        public static final Property endedAtTime = m_model.createProperty("http://www.w3.org/ns/prov/endedAtTime");
        public static final Property agent = m_model.createProperty("http://www.w3.org/ns/prov/agnet");
        public static final Property wasAssociatedWith = m_model.createProperty("http://www.w3.org/ns/prov/Status");
        public static final Property qualifiedAssociation = m_model.createProperty("http://www.w3.org/ns/prov/qualifiedAssociation");
        public static final Property Association = m_model.createProperty("http://www.w3.org/ns/prov/Association");
        public static final Property hadPlan = m_model.createProperty("http://www.w3.org/ns/prov/hadPlan");
        public static final Property wasGeneratedBy = m_model.createProperty("http://www.w3.org/ns/prov/wasGeneratedBy");
        public static final Property Plan = m_model.createProperty("http://www.w3.org/ns/prov/Plan");
        public static final Property Entity = m_model.createProperty("http://www.w3.org/ns/prov/Entity");
        public static final Property Activity = m_model.createProperty("http://www.w3.org/ns/prov/Activity");
        public static final Property used = m_model.createProperty("http://www.w3.org/ns/prov/used");

}

