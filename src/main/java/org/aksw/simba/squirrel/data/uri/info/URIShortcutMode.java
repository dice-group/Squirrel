package org.aksw.simba.squirrel.data.uri.info;

/**
 * The mode for storing the {@link java.net.URI}s (keys)
 * TOTAL_URI: tho whole URI
 * NAMESPACE: only the Namespace. It's not official defined, I define it in following way:
 * - protocol://url#appendix => protocol://url
 * - protocol://url?params => protocol://url
 * - protocol://url/path => url
 * DOMAIN: only the domain
 *
 * @author Pilipp Heinisch
 */
public enum URIShortcutMode {
    TOTAL_URI, NAMESPACE, ONLY_DOMAIN
}
