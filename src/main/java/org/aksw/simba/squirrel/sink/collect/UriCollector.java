package org.aksw.simba.squirrel.sink.collect;

import java.util.Iterator;

import org.aksw.simba.squirrel.sink.SinkDecorator;

public interface UriCollector extends SinkDecorator {

    public Iterator<String> getUris();

    public void reset();
}
