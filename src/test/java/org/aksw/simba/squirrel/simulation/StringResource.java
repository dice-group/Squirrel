package org.aksw.simba.squirrel.simulation;

import java.io.OutputStream;

import org.apache.jena.riot.Lang;

import com.hp.hpl.jena.rdf.model.Model;

public class StringResource extends AbstractCrawleableResource {

    private final Lang serializationLang;

    public StringResource(Model model, String resourceName, Lang serializationLang) {
        super(model, resourceName, serializationLang.getContentType().toString());
        this.serializationLang = serializationLang;
    }

    @Override
    protected void serializeModel(OutputStream out, Model model) {
        model.write(out, serializationLang.getName());
    }

}
