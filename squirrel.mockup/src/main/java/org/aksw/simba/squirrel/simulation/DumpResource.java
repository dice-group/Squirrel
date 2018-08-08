package org.aksw.simba.squirrel.simulation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple string-based resource that offers the given RDF model as String with
 * the given RDF serialization (language).
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DumpResource extends AbstractCrawleableResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumpResource.class);

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
    public DumpResource(Model model, String resourceName, Lang serializationLang) {
        super(model, resourceName, "application/gzip");
        this.serializationLang = serializationLang;
    }

    @Override
    protected void serializeModel(OutputStream out, Model model) {
        try {
            GZIPOutputStream gOut = new GZIPOutputStream(out);
            model.write(gOut, serializationLang.getName());
        } catch (IOException e) {
            LOGGER.error("Exception while creating a GZIP output stream. The output will be empty.", e);
        }
    }

}
