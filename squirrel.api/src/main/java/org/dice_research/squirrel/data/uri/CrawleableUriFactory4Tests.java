package org.dice_research.squirrel.data.uri;

import java.net.InetAddress;
import java.net.URI;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.dice_research.squirrel.data.uri.UriType;

public class CrawleableUriFactory4Tests extends CrawleableUriFactoryImpl {

    public CrawleableUri create(URI uri, InetAddress ipAddress, UriType type) {
        return new CrawleableUri(uri, ipAddress, type);
    }

}
