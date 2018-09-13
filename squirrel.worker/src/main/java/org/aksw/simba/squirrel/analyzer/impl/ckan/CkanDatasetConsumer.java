package org.aksw.simba.squirrel.analyzer.impl.ckan;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.aksw.simba.squirrel.collect.UriCollector;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.vocab.DCAT;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.tika.metadata.DublinCore;
import org.apache.xerces.util.URI;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.traceprov.dcat.DcatDataset;

/**
 * A simple consumer of {@link CkanDataset} objects transforming them into RDF
 * triples and writing the triples to the given {@link Sink} and
 * {@link UriCollector}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class CkanDatasetConsumer implements Consumer<CkanDataset> {

    protected Sink sink;
    protected UriCollector collector;
    protected CrawleableUri curi;

    public CkanDatasetConsumer(Sink sink, UriCollector collector, CrawleableUri curi) {
        super();
        this.sink = sink;
        this.collector = collector;
        this.curi = curi;
    }

    /**
     * This consumer method maps a single {@link CkanDataset} object to set of RDF triples. The mapping is based on the mapping from {@link http://extensions.ckan.org/extension/dcat/#rdf-dcat-to-ckan-dataset-mapping}. 
     */
    @Override
    public void accept(CkanDataset dataset) {
        if (dataset == null) {
            return;
        }
        // Make sure that we create the map only once
        Map<String, String> extras = dataset.getExtrasAsHashMap(); 
        
        // Create resource for dataset
        Resource datasetRes = createDatasetResource(dataset, extras);
        store(datasetRes, RDF.type, DCAT.Dataset);
    }
    
    protected Resource createDatasetResource(CkanDataset dataset, Map<String, String> extras) {
        String uri = null;
        if(extras.containsKey("uri")) {
            uri = extras.get("uri");
        }
        // If the URI is not well formed, create an artificial URI
        if(!URI.isWellFormedAddress(uri)) {
            uri = curi.getUri().toString();
            if((!uri.endsWith("/")) && (!uri.endsWith("#"))) {
                uri += "/";
            }
            uri += dataset.getId();
        }
        return ResourceFactory.createResource(uri);
    }

    protected void store(Resource s, Property p, RDFNode o) {
        Triple t = new Triple(s.asNode(), p.asNode(), o.asNode());
        sink.addTriple(curi, t);
        collector.addTriple(curi, t);
    }
}
