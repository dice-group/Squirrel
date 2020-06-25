package org.dice_research.squirrel;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryDataset;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Before;

public class MongoDBScoreBasedTest extends MongoDBBasedTest {

    protected QueryExecutionFactory queryExecFactory;

    @Before
    public void setUp() throws Exception {
        Dataset dataset = DatasetFactory.create();
        dataset.setDefaultModel(ModelFactory.createDefaultModel());
        Node g = NodeFactory.createURI(Constants.DEFAULT_META_DATA_GRAPH_URI.toString());
        Node s1 = NodeFactory.createURI("http://dbpedia.org/resource/New_York_City");
        Node s2 = NodeFactory.createURI("http://dbpedia.org/resource/Berlin");
        Node s3 = NodeFactory.createURI("http://dbpedia.org/resource/Bangalore");
        Node s4 = NodeFactory.createURI("http://dbpedia.org/resource/Moscow");
        Node s5 = NodeFactory.createURI("https://www.lonelyplanet.com/germany/paderborn");
        Node s6 = NodeFactory.createURI("https://www.lonelyplanet.com/germany/north-rhine-westphalia/dortmund");
        Node s7 = NodeFactory.createURI("https://www.lonelyplanet.com/england/london");
        Node s8 = NodeFactory.createURI("https://www.lonelyplanet.com/france/paris");

        String otherUri = "http://dbpedia.org/doesntMatter";
        Node o = NodeFactory.createURI("http://dbpedia.org/doesntMatter");
        DatasetGraph graph = dataset.asDatasetGraph();

        for (int i = 0; i < 5; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s1, p, o); // <http://dbpedia.org/resource/New_York_City>
        }
        for (int i = 0; i < 10; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s2, p, o); // <http://dbpedia.org/resource/Berlin>
        }
        for (int i = 0; i < 8; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s3, p, o); // <http://dbpedia.org/resource/Bangalore>
        }
        for (int i = 0; i < 4; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s4, p, o); // <http://dbpedia.org/resource/Moscow>
        }
        for (int i = 0; i < 4; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s5, p, o); // <https://www.lonelyplanet.com/germany/paderborn>
        }
        for (int i = 0; i < 5; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s6, p, o); // <https://www.lonelyplanet.com/germany/north-rhine-westphalia/dortmund>
        }
        for (int i = 0; i < 4; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s7, p, o); // <https://www.lonelyplanet.com/england/london>
        }
        for (int i = 0; i < 7; i++) {
            Node p = NodeFactory.createURI(otherUri + i);
            graph.add(g, s8, p, o); // <https://www.lonelyplanet.com/france/paris>
        }
        queryExecFactory = new QueryExecutionFactoryDataset(dataset);
    }
}
