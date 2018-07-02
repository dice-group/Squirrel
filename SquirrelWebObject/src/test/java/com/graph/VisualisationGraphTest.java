package com.graph;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

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
}