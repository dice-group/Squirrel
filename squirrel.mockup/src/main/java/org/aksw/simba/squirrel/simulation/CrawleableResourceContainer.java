package org.aksw.simba.squirrel.simulation;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link Container} implementation that can be used to host
 * {@link CrawleableResource} instances.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class CrawleableResourceContainer implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawleableResourceContainer.class);

    /**
     * Resources hosted by this container.
     */
    private Map<String, CrawleableResource> resources = new HashMap<String, CrawleableResource>();

    /**
     * Constructor.
     * 
     * @param resources resources that should be hosted by this container.
     */
    public CrawleableResourceContainer(CrawleableResource... resources) {
        URI uri;
        String temp;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < resources.length; ++i) {
            builder.delete(0, builder.length());
            try {
                uri = new URI(resources[i].getResourceName());
                temp = uri.getPath();
                if (temp != null) {
                    builder.append(temp);
                }
                temp = uri.getQuery();
                if (temp != null) {
                    builder.append(temp);
                }
                temp = uri.getFragment();
                if (temp != null) {
                    builder.append(temp);
                }
                this.resources.put(builder.toString(), resources[i]);
            } catch (URISyntaxException e) {
                LOGGER.warn("Couldn't parse URI. It might not be reachable", e);
                this.resources.put(resources[i].getResourceName(), resources[i]);
            }
        }
    }

    @Override
    public void handle(Request request, Response response) {
        if (resources.containsKey(request.getTarget())) {
            CrawleableResource resource = resources.get(request.getTarget());
            OutputStream out = null;
            try {
                out = response.getOutputStream();
                response.setCode(Status.OK.code);
                response.setValue("Content-Type", resource.getResourceContentType());
                resource.writeResourceData(out);
            } catch (Exception e) {
                LOGGER.error("Got exception.", e);
            } finally {
                IOUtils.closeQuietly(out);
            }
        } else {
            LOGGER.warn("Got a request for an unknown URL: \"" + request.getAddress() + "\".");
            response.setCode(Status.BAD_REQUEST.code);
            try {
                response.getOutputStream().close();
            } catch (Exception e) {
                // nothing to do
            }
        }
    }

}
