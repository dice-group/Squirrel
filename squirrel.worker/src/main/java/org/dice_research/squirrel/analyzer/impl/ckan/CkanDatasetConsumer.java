package org.dice_research.squirrel.analyzer.impl.ckan;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.xerces.util.URI;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.encoder.TripleEncoder;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.vocab.DCAT;
import org.dice_research.squirrel.vocab.VCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanPair;
import eu.trentorise.opendata.jackan.model.CkanResource;

/**
 * A simple consumer of {@link CkanDataset} objects transforming them into RDF
 * triples and writing the triples to the given {@link Sink} and
 * {@link UriCollector}.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class CkanDatasetConsumer implements Consumer<CkanDataset> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CkanDatasetConsumer.class);

    protected Sink sink;
    protected UriCollector collector;
    protected CrawleableUri curi;
    protected String curiString;
    protected TripleEncoder tripleEncoder;

    public CkanDatasetConsumer(Sink sink, UriCollector collector, CrawleableUri curi,TripleEncoder tripleEncoder) {
        super();
        this.sink = sink;
        this.collector = collector;
        this.curi = curi;
        this.curiString = curi.getUri().toString();
        this.tripleEncoder = tripleEncoder;
    }

    /**
     * This consumer method maps a single {@link CkanDataset} object to set of RDF
     * triples. The mapping is based on the mapping from
     * {@link http://extensions.ckan.org/extension/dcat/#rdf-dcat-to-ckan-dataset-mapping}.
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

        storeTextLiteral(datasetRes, DCTerms.title, true, dataset.getTitle());
        storeTextLiteral(datasetRes, DCTerms.description, true, dataset.getNotes());
        storeTextLiteral(datasetRes, DCAT.keyword, false,
                dataset.getTags().stream().map(t -> t.getName()).collect(Collectors.toList()).toArray(new String[0]));
        storeTextLiteral(datasetRes, DCTerms.identifier, true, extras.get("identifier"), extras.get("guid"),
                dataset.getId());
        storeTextLiteral(datasetRes, OWL.versionInfo, false, dataset.getVersion(), extras.get("dcat_version"));
        storeTextLiteral(datasetRes, DCTerms.provenance, true, extras.get("provenance"));

        storeResourceOrText(datasetRes, DCAT.landingPage, true, dataset.getUrl());
        if (dataset.getLicenseUrl() != null || dataset.getLicenseTitle() != null)
            storeResourceOrText(datasetRes, DCTerms.license, true, dataset.getLicenseUrl(), dataset.getLicenseTitle());

        storeTypedLiteral(datasetRes, DCTerms.issued, XSDDatatype.XSDdateTimeStamp, true, extras.get("issued"),
                dataset.getMetadataCreated());
        storeTypedLiteral(datasetRes, DCTerms.modified, XSDDatatype.XSDdateTimeStamp, true, extras.get("modified"),
                dataset.getMetadataModified());
        storeTypedLiteral(datasetRes, DCTerms.accrualPeriodicity, XSDDatatype.XSDinteger, true,
                extras.get("frequency"));

        if (dataset.getExtras() != null)
            for (String theme : findTheme(dataset.getExtras())) {
                storeResourceOrText(datasetRes, DCAT.theme, true, theme);
            }

        storePublisher(dataset, datasetRes, extras);
        storeContact(datasetRes, dataset, extras);
        storeResources(datasetRes, dataset);
    }

    protected void storePublisher(CkanDataset dataset, Resource datasetRes, Map<String, String> extras) {
//        if (!extras.containsKey("publisher_uri")) {
//            return;
//        }
        String uri = curiString;
        if ((!uri.endsWith("/")) && (!uri.endsWith("#"))) {
            uri += "/";
        }
        uri += "organization/";
        uri += dataset.getId();

        Resource publisher = ResourceFactory.createResource(uri);
        store(datasetRes, DCTerms.publisher, publisher);
        store(publisher, RDF.type, FOAF.Agent);
        storeTextLiteral(publisher, FOAF.name, true, extras.get("publisher_name"));
        storeTextLiteral(publisher, FOAF.mbox, true, extras.get("publisher_email"));
        storeTextLiteral(publisher, DCTerms.type, true, extras.get("publisher_type"));
        storeResourceOrText(publisher, FOAF.homepage, true, extras.get("publisher_url"));
    }

    protected void storeContact(Resource datasetRes, CkanDataset dataset, Map<String, String> extras) {
        if (!extras.containsKey("contact_uri")) {
            return;
        }
        String uri = extras.get("contact_uri");
        if (!URI.isWellFormedAddress(uri)) {
            return;
        }
        Resource contact = ResourceFactory.createResource(uri);
        store(datasetRes, DCTerms.publisher, contact);
        store(contact, RDF.type, VCard.Kind);
        storeTextLiteral(contact, VCard.fn, true, extras.get("contact_name"), dataset.getMaintainer(),
                dataset.getAuthor());
        storeResourceOrText(contact, VCard.hasEmail, true, extras.get("contact_email"), dataset.getMaintainerEmail(),
                dataset.getAuthorEmail());
    }

    protected void storeResources(Resource datasetRes, CkanDataset dataset) {
        if ((dataset.getResources() != null) && (dataset.getNumResources() > 0)) {
            for (CkanResource ckanResource : dataset.getResources()) {
                storeResource(datasetRes, ckanResource);
            }
        }
    }

    protected void storeResource(Resource datasetRes, CkanResource ckanResource) {
        String uri = curiString;
        if ((!uri.endsWith("/")) && (!uri.endsWith("#"))) {
            uri += "/";
        }
        uri += "distribution/";
        uri += ckanResource.getId();
        Resource resource = ResourceFactory.createResource(uri);
        store(datasetRes, DCAT.distribution, resource);
        store(resource, RDF.type, DCAT.Distribution);

        storeTextLiteral(resource, DCTerms.title, true, ckanResource.getName());
        storeTextLiteral(resource, DCTerms.description, true, ckanResource.getDescription());
        storeTextLiteral(resource, DCAT.mediaType, true, ckanResource.getMimetype());
        storeTextLiteral(resource, DCTerms.format, true, ckanResource.getFormat());

        try {
            storeResourceOrText(resource, DCTerms.license, true, findLicense(ckanResource));
        } catch (NullPointerException e) {
        }

        storeResourceOrText(resource, DCAT.accessURL, true, ckanResource.getUrl().replaceAll("\\s+", "%20"));
        storeResourceOrText(resource, DCAT.downloadURL, true, ckanResource.getUrl().replaceAll("\\s+", "%20"));

        storeTypedLiteral(resource, DCAT.byteSize, XSDDatatype.XSDlong, true, ckanResource.getSize());
        storeTypedLiteral(resource, DCTerms.issued, XSDDatatype.XSDdateTimeStamp, true, ckanResource.getCreated());
        storeTypedLiteral(resource, DCTerms.modified, XSDDatatype.XSDdateTimeStamp, true,
                ckanResource.getLastModified());
    }

    protected List<String> findTheme(List<CkanPair> pairList) {
        String theme = "";

        for (CkanPair pair : pairList) {
            if ("theme".equals(pair.getKey())) {
                theme = pair.getValue().replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
                break;
            }
        }
        return Arrays.asList(theme.split(","));
    }

    protected String findLicense(CkanResource resource) {
        return resource.getOthers().get("license").toString();

    }

    /**
     * Method creating one or several literals based on the given data and storing
     * it as object of the given subject and predicate.
     * 
     * @param subject         the resource which should be used as subject
     * @param predicate       the property which should be used as predicate
     * @param storeFirstMatch a flag indicating whether the first non null value of
     *                        the literals is used or if all non null literals
     *                        should be stored (in mutliple triples)
     * @param literals        the list of literals which will be taken into account
     */
    protected void storeTextLiteral(Resource subject, Property predicate, boolean storeFirstMatch, String... literals) {
        for (int i = 0; i < literals.length; i++) {
            if (literals[i] != null) {
                store(subject, predicate, ResourceFactory.createStringLiteral(literals[i]));
                if (storeFirstMatch) {
                    return;
                }
            }
        }
    }

    private void storeTypedLiteral(Resource subject, Property predicate, RDFDatatype datatype, boolean storeFirstMatch,
            Object... literals) {
        for (int i = 0; i < literals.length; i++) {
            if (literals[i] != null) {
                if (datatype.isValidValue(literals[i])) {
                    store(subject, predicate, ResourceFactory.createTypedLiteral(datatype.cannonicalise(literals[i])));
                } else {
                    String string = literals[i].toString();
                    if (datatype.isValid(string)) {
                        store(subject, predicate, ResourceFactory.createTypedLiteral(string, datatype));
                    } else {
                        LOGGER.info(
                                "Couldn't find a representation of \"{}\" fitting to the datatype {}. Adding it as a simple String literal.",
                                string, datatype.getURI());
                        store(subject, predicate, ResourceFactory.createStringLiteral(string));
                    }
                }
                if (storeFirstMatch) {
                    return;
                }
            }
        }
    }

    private void storeResourceOrText(Resource subject, Property predicate, boolean storeFirstMatch, String... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                if (URI.isWellFormedAddress(values[i])) {
                    store(subject, predicate, ResourceFactory.createResource(values[i]));
                } else {
                    store(subject, predicate, ResourceFactory.createStringLiteral(values[i]));
                }
                if (storeFirstMatch) {
                    return;
                }
            }
        }
    }

    protected Resource createDatasetResource(CkanDataset dataset, Map<String, String> extras) {
        String uri = null;
        if (extras.containsKey("uri")) {
            uri = extras.get("uri");
        }
        // If the URI is not well formed, create an artificial URI
        if (!URI.isWellFormedAddress(uri)) {
            uri = curiString;
            if ((!uri.endsWith("/")) && (!uri.endsWith("#"))) {
                uri += "/";
            }
            uri += dataset.getId();
        }
        return ResourceFactory.createResource(uri);
    }

    protected void store(Resource s, Property p, RDFNode o) {
        Triple t = new Triple(s.asNode(), p.asNode(), o.asNode());
        URL url = null;
        try {
            url = new URL(o.toString());
        } catch (MalformedURLException e) {

        }
        if (url != null) {
            t = new Triple(s.asNode(), p.asNode(), NodeFactory.createURI(url.toString()));
        }
        
        t = tripleEncoder.encodeTriple(t);
        
        sink.addTriple(curi, t);
        // We already know most of the Resources, so make sure that they are not part of
        // our current dataset

        collector.addTriple(curi, t);
//        if (!s.getURI().startsWith(curiString)) {
//            collector.addNewUri(curi, s.getURI());
//        }
//        if (o.isURIResource() && (!s.getURI().startsWith(curiString))) {
//            collector.addNewUri(curi, t.getObject());
//        }
    }

}
