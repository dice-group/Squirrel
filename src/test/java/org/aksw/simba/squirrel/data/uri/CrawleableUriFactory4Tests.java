package org.aksw.simba.squirrel.data.uri;

import java.net.InetAddress;
import java.net.URI;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.aksw.simba.squirrel.data.uri.UriType;

public class CrawleableUriFactory4Tests extends CrawleableUriFactoryImpl {

    public CrawleableUri create(URI uri, InetAddress ipAddress, UriType type) {
        return new CrawleableUri(uri, ipAddress, type);
    }

}
