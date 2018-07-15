package com.graph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class VisualisationHelper {
    /**
     * converts a byte stream into a {@link VisualisationGraph}. If there are any exceptions, the methods tries to handle them
     *
     * @param bytes the byte stream
     * @return a {@link VisualisationGraph}, that was in further times converted into a byte stream
     */
    public static VisualisationGraph convertToObject(byte[] bytes) {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return (VisualisationGraph) o.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return new VisualisationGraph();
            }
        } catch (IOException e) {
            e.printStackTrace();
            VisualisationGraph ret = new VisualisationGraph();
            ret.addNode("Converting fail", e.hashCode() + "");
            ret.addNode(e.getMessage());
            ret.addEdge("Converting fail", e.getMessage());
            return ret;
        }
    }
}
