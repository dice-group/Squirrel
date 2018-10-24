package org.dice_research.squirrel.sink;

import org.dice_research.squirrel.sink.Sink;

public interface SinkDecorator extends Sink {

    public Sink getDecorated();
}
