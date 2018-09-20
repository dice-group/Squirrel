package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.Constants;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;

/**
 * A simple utilities class for working with the {@link CrawlingActivity}
 * objects.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class ActivityUtil {

    /**
     * A simple method which attaches a step with the given Class and the given
     * actions to the {@link CrawlingActivity} of the given URI if it exists.
     * 
     * @param uri
     *            the URI that is crawled and to which the given actions should be
     *            added
     * @param clazz
     *            the Class of the calling object
     * @param actions
     *            the actions the object when handling the given URI
     */
    public static void addStep(CrawleableUri uri, Class<?> clazz, String... actions) {
        CrawlingActivity activity = (CrawlingActivity) uri.getData(Constants.URI_CRAWLING_ACTIVITY);
        if (activity != null) {
            activity.addStep(clazz, actions);
        }
    }

    /**
     * A simple method which attaches a step with the given Class to the {@link CrawlingActivity} of the given URI if it exists.
     * 
     * @param uri
     *            the URI that is crawled and to which the given actions should be
     *            added
     * @param clazz
     *            the Class of the calling object
     */
    public static void addStep(CrawleableUri uri, Class<?> clazz) {
        addStep(uri, clazz,(String[]) null);
    }
}
