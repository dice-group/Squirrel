package com.graph;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VisualisationGraphTest {

    VisualisationGraph graph = new VisualisationGraph();

    @Before
    public void setUp() throws Exception {
        graph.addNode("Test 1");
        graph.addNode("Test 2");
        graph.addNode("Test 2", "1.1.1.1");

        graph.addEdge("Test 1", "Test 2");
        graph.addEdge("Test 2", "Test 1");
    }

    @Test
    public void optimizeArrays() {
        assertEquals(8, graph.getEdges().length);
        graph.optimizeArrays();
        assertEquals(2, graph.getEdges().length);
    }

    @Test
    @Deprecated
    public void convertToByteStream() {
        VisualisationGraph converted = VisualisationHelper.convertToObject(graph.convertToByteStream());

        assertThat(converted.getEdges()[0].toString(), CoreMatchers.equalTo(graph.getEdges()[0].toString()));
        assertThat(converted.getEdges()[1].toString(), CoreMatchers.equalTo(graph.getEdges()[1].toString()));
        assertEquals(graph.getNode("Test 1").toInt(), converted.getNode("Test 1").toInt());
    }

    @Test
    public void getEdges() {
        assertThat(graph.getEdges()[0].toString(), CoreMatchers.equalTo("Test 1 -> Test 2 (1.1.1.1)"));
        assertThat(graph.getEdges()[1].toString(), CoreMatchers.equalTo("Test 2 (1.1.1.1) -> Test 1"));
    }

    @Test
    public void getEdges1() {
        graph.optimizeArrays();
        assertArrayEquals(graph.getEdges(), graph.getEdges(graph.getNode("Test 1")));
    }

    @Test
    public void equals() {
        VisualisationGraph graph1 = new VisualisationGraph();
        VisualisationGraph graph2 = new VisualisationGraph();

        assertTrue("2 fresh visualization (empty) graphs should be equal", graph1.equals(graph2));

        graph1.addNode("https://www.philippheinisch.de", "1.1.1.1");
        graph1.addEdge("https://www.philippheinisch.de", "https://www.bibleserver.com/");
        graph2.addEdge("https://www.philippheinisch.de", "https://www.bibleserver.com/");

        assertTrue("Adding a node pair (even over different circumstances should not de-equals the graphs!", graph1.equals(graph2));

        graph1.optimizeArrays();

        assertFalse("Nut optimazing the Arrays aof only one Graph should result in a nom equal result", graph1.equals(graph2));

        graph2.optimizeArrays();
        ;
        graph1.addNode("https://philippheinisch.de/aboutMe.html");
        graph2.addNode("https://philippheinisch.de/aboutMe.html");

        assertTrue("Final test with extend the arrays: new node", graph1.equals(graph2));
    }
}
