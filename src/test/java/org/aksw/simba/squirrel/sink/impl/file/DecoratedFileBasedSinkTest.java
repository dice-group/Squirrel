package org.aksw.simba.squirrel.sink.impl.file;

import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.collect.SimpleUriCollector;

public class DecoratedFileBasedSinkTest extends FileBasedSinkTest {

    protected Sink createSink(boolean useCompression) {
        return new SimpleUriCollector(new FileBasedSink(tempDirectory, useCompression));
    }
}
