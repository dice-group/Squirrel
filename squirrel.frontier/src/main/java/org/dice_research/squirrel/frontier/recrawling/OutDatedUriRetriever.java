package org.dice_research.squirrel.frontier.recrawling;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import com.sun.jndi.toolkit.url.Uri;
import org.dice_research.squirrel.data.uri.CrawleableUri;

public interface OutDatedUriRetriever{

    /**
     * Returns all {@link CrawleableUri}s which are crawled a week ago and have to be recrawled.
     *
     * @return The outdated {@link CrawleableUri}s.
     */
    List<CrawleableUri> getUriToRecrawl();
}
