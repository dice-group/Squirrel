package org.aksw.simba.squirrel.sink;

public interface SinkDecorator extends Sink {

    public Sink getDecorated();
}
