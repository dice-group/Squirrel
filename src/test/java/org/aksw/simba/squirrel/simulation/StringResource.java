package org.aksw.simba.squirrel.simulation;

import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

/**
 * Simple string-based resource that offers the given RDF model as String with
 * the given RDF serialization (language).
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class StringResource extends AbstractCrawleableResource {

    /**
     * Serialization language used to serialize the RDF model.
     */
    private final Lang serializationLang;

    /**
     * Constructor.
     * 
     * @param model
     *            the RDF data of the resource
     * @param resourceName
     *            the name of the resource
     * @param serializationLang
     *            the serialization used to write the RDF data
     */
    public StringResource(Model model, String resourceName, Lang serializationLang) {
        super(model, resourceName, serializationLang.getContentType().getContentType());
        this.serializationLang = serializationLang;
    }

    @Override
    protected void serializeModel(OutputStream out, Model model) {
        model.write(out, serializationLang.getName());
    }

}
