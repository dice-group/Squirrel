package com.graph;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class VisualisationNode implements Serializable {

    private String id;
    private String uri;
    private String ip;
    public int x, y;
    public final int size = 3;
    private Color color;

    @SuppressWarnings("unused")
    public VisualisationNode(int id, String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri must not be null! (ID " + id + ")");
        }

        this.id = "n" + id;
        x = id;
        y = id % 5;
        this.uri = uri;
        ip = "unknown";
        color = Color.LIGHT_GRAY;
    }

    public VisualisationNode(int id, String uri, String ip) {
        if (uri == null || ip == null) {
            throw new IllegalArgumentException("uri and ip must not be null! (" + uri + "/" + ip + ")");
        }

        this.id = "n" + id;
        x = id;
        y = 5 + id % 5;
        this.uri = uri;
        this.ip = ip;
        color = Color.GREEN;
    }

    public String getId() {
        return id;
    }

    void setIp(String ip) {
        if (ip == null) {
            throw new IllegalArgumentException("ip must not be null! (tried to reset the ip " + this.ip + " from the node" + this + " )");
        }

        this.ip = ip;
    }

    public String getUri() {
        return uri;
    }

    public String getLabel() {
        return getUri();
    }

    public String getIp() {
        return ip;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getColor() {
        String red = Integer.toHexString(color.getRed());
        String green = Integer.toHexString(color.getGreen());
        String blue = Integer.toHexString(color.getBlue());
        return "#" + ((red.length() == 1) ? "0" + red : red) + ((green.length() == 1) ? "0" + green : green) + ((blue.length() == 1) ? "0" + blue : blue);
    }

    @Override
    public String toString() {
        return uri + ((ip.equals("unknown")) ? "" : " (" + ip + ")");
    }

    public int toInt() {
        return uri.hashCode();
    }

    @SuppressWarnings("all")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisualisationNode)) return false;
        VisualisationNode that = (VisualisationNode) o;
        return size == that.size &&
            Objects.equals(getUri(), that.getUri()) &&
            Objects.equals(getColor(), that.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri(), size, getColor());
    }
}
