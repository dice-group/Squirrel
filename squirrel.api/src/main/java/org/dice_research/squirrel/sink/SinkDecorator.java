package org.dice_research.squirrel.sink;

public interface SinkDecorator extends Sink {

    public Sink getDecorated();
}
