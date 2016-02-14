package org.aksw.simba.squirrel.sink.collect;

import java.util.Set;

import org.aksw.simba.squirrel.sink.SinkDecorator;

public interface UriCollector extends SinkDecorator {

    public Set<String> getUris();

    public void reset();
}
