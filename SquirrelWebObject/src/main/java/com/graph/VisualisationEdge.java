package com.graph;

import java.io.Serializable;

public class VisualisationEdge implements Serializable {

    private String id;
    private VisualisationNode source, target;
    private int weight;

    public VisualisationEdge(int id, VisualisationNode source, VisualisationNode target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("source and target param must not be null! (" + source + " -> " + target + ")");
        }

        this.id = "e" + id;
        this.source = source;
        this.target = target;
        weight = 1;
    }

    public void addWeight() {
        weight++;
    }

    public String getId() { return id; }

    public String getSource() { return source.getId(); }

    public VisualisationNode getSourceNode() {
        return source;
    }

    public String getTarget() { return target.getId(); }

    public VisualisationNode getTargetNode() {
        return target;
    }

    @Override
    public String toString() {
        return source + " -> " + target;
    }
}
