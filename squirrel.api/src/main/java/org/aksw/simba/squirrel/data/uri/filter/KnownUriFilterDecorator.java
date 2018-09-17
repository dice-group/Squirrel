package org.aksw.simba.squirrel.data.uri.filter;

import java.io.Closeable;

public interface KnownUriFilterDecorator extends KnownUriFilter, Closeable {

    public KnownUriFilter getDecorated();
}
