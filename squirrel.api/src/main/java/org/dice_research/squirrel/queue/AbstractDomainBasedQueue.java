package org.dice_research.squirrel.queue;

import java.net.URISyntaxException;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.UriUtils;
import org.dice_research.squirrel.data.uri.group.UriGroupByOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class manages two important aspects of an DomainBasedQueue.
 * It uses a mutex to access the queue and it manages a set containing Domains that
 * are currently blocked by one of the workers.
 *
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *  */

public abstract class AbstractDomainBasedQueue extends AbstractGroupingQueue<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDomainBasedQueue.class);
    
    protected final static String DEFAULT_DOMAIN = "default";

    public AbstractDomainBasedQueue() {
        super(new UriGroupByOperator<String>() {
            @Override
            public String retrieveKey(CrawleableUri uri) {
                try {
                return UriUtils.getDomainName(uri.getUri().toString());
                } catch (URISyntaxException e) {
                    LOGGER.error("Could not obtain domain from URI: " + uri.getUri().toString() + ". Using Default");
                    return DEFAULT_DOMAIN;
                }
            }
        });
    }

}
