package org.aksw.simba.squirrel.data.uri.serialize;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

public interface CrawleableUriSerializer {

    public String serialize(CrawleableUri uri);
    
    public CrawleableUri deserialize(String data);
}
