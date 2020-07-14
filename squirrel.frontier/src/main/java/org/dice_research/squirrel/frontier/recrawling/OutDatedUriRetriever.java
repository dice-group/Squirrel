package org.dice_research.squirrel.frontier.recrawling;

import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;

public interface OutDatedUriRetriever{

    /**
     * Returns all {@link CrawleableUri}s which are crawled a week ago and have to be recrawled.
     *
     * @return The outdated {@link CrawleableUri}s.
     */
    List<CrawleableUri> getUriToRecrawl();
}
