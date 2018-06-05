package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.metadata.CrawlingActivity;
import org.apache.jena.rdf.model.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;


public class provenance {
    private static final String prov = "<http://www.w3.org/ns/prov-o#>";
    private static final String sq = "<https://www.w3id.org/squirrel/vocab#>";
    private String datasetPrefix = "http://sparqlHost:3030/";
    String strMetaDatasetUriUpdate = datasetPrefix + "MetaData/update";

    public void addMetadata(CrawlingActivity crawlingActivity) {
        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("prov", "<http://www.w3.org/ns/prov-o#> ");
        model.setNsPrefix("sq", "<https://www.w3id.org/squirrel/vocab#> ");
        Resource CrawlingActivity = ResourceFactory.createResource(crawlingActivity.getId().toString());
        model.add(CrawlingActivity, p1("startedAtTime"), crawlingActivity.getDateStarted());
        model.add(CrawlingActivity, p1("endedAtTime"), crawlingActivity.getDateEnded());
        model.add(CrawlingActivity, p1("status"), crawlingActivity.getStatus().toString());
        model.add(CrawlingActivity, p1("wasAssociatedWith"), l(crawlingActivity.getWorker().getId()));
        model.add(CrawlingActivity, p2("numberOfTriples"), l(crawlingActivity.getNumTriples()));
        model.add(CrawlingActivity, p2("hostedOn"), datasetPrefix);
        String uri = crawlingActivity.geturi().toString().replace("\"", "");
        model.add(CrawlingActivity, p1("wasGeneratedBy"), uri);
        UpdateRequest request = UpdateFactory.create(QueryGenerator.getInstance().getAddQuery(crawlingActivity.uri, model));
        UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, strMetaDatasetUriUpdate);
        proc.execute();
    }

    private static Literal l(Object value) {
        return ResourceFactory.createTypedLiteral(value);
    }

    public String getAddQuery(CrawleableUri uri, Model model)
    {
        StringBuilder stringBuilder = new StringBuilder();
        StmtIterator iter = model.listStatements();
        stringBuilder.append("INSERT { Graph <");
        stringBuilder.append(uri.getUri());
        stringBuilder.append(">");
        Statement stmt      = iter.nextStatement();  // get next statement
        Resource subject   = stmt.getSubject();     // get the subject
        Property predicate = stmt.getPredicate();   // get the predicate
        RDFNode   object    = stmt.getObject();      // get the object
        while(iter.hasNext())
        {
            stringBuilder.append("{");
            stringBuilder.append("<");
            stringBuilder.append(subject.toString());
            stringBuilder.append("> <");
            stringBuilder.append(predicate.toString());
            stringBuilder.append("> <");
            stringBuilder.append(object.toString());
            stringBuilder.append("> .");

        }
        return stringBuilder.toString();


    }

    private static Property p1(String localname) {
        return ResourceFactory.createProperty(prov + localname);
    }

    private static Property p2(String localname) {
        return ResourceFactory.createProperty(sq + localname);
    }
}

