package org.aksw.simba.squirrel.data.uri.serialize;

import java.io.IOException;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public interface CrawleableUriSerializer {

    public byte[] serialize(CrawleableUri uri) throws IOException;
    
    public CrawleableUri deserialize(byte[] data) throws IOException;
}
