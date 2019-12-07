package org.dice_research.squirrel.data.uri.norm;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class UriVariantImpl implements UriVariants{


    @Override
    public List<CrawleableUri> getUriVariants(CrawleableUri uri) {
        List<CrawleableUri> variantUris = new ArrayList<>();
        URI uriObject = uri.getUri();
        String domain = uriObject.getHost();
        try {
            URI newUri = new URI(domain);
            CrawleableUri uriVariant = new CrawleableUri(newUri);
            variantUris.add(uriVariant);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return variantUris;
    }
}
