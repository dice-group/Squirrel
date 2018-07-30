package org.aksw.simba.squirrel.data.uri.info;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Common helper methods for implementations of {@link URIReferences}
 *
 * @author Philipp Heinisch
 */
class URIReferencesUtils {

    private URIShortcutMode uriShortcutMode;

    URIReferencesUtils() {
        this(URIShortcutMode.NAMESPACE);
    }

    /**
     * creates a new instance of {@link URIReferencesUtils} (useful help methods)
     *
     * @param uriShortcutMode the {@link URIShortcutMode}. Default is URIShortcutMode.NAMESPACE
     */
    URIReferencesUtils(URIShortcutMode uriShortcutMode) {
        this.uriShortcutMode = uriShortcutMode;
    }

    /**
     * Merge to list with applying the convertedURI method
     *
     * @param originList the original list (list one). Can be {@code null}.
     * @param newList    the list, that should be merged in. Can be {@code null}.
     * @return the merged result. Is not {@code null}.
     */
    List<String> mergeLists(List<String> originList, List<CrawleableUri> newList) {
        originList = (originList == null) ? Collections.emptyList() : originList;
        List<String> newListStrings = (newList == null) ? Collections.emptyList() : newList.stream().map(this::convertURI).collect(Collectors.toList());
        HashSet<String> ret = new HashSet<>((originList.size() >= newListStrings.size()) ? originList : newListStrings);

        ret.addAll(((originList.size() >= newListStrings.size()) ? newListStrings : originList));

        return new ArrayList<>(ret);
    }

    /**
     * Converts an URI to a String regarding the selected uriShortcutMode of the instance of this class
     *
     * @param uri the {@link URI}, that should be converted
     * @return a (shorten) string.
     * For example, if you have the URI https://philippheinisch.de/programs.html#tagebuchprogramm, this method would return:
     * - TOTAL_URI: https://philippheinisch.de/programs.html#tagebuchprogramm
     * - NAMESPACE: https://philippheinisch.de/programs.html
     * - ONLY_DOMAIN: philippheinisch.de
     */
    String convertURI(CrawleableUri uri) {
        String ret = uri.getUri().toString();
        switch (uriShortcutMode) {
            case TOTAL_URI:
                return ret;
            case NAMESPACE:
                int lastCross = ret.lastIndexOf('#');
                int lastQuestionMark = ret.lastIndexOf('?');
                int lastSlash = ret.lastIndexOf('/');
                boolean protocolSlash = ret.contains("//");
                if (lastCross == -1 && lastQuestionMark == -1) {
                    if (protocolSlash && lastSlash <= 8) {
                        //was only the pure domain
                        return (lastSlash == ret.length() - 1) ? ret.substring(0, ret.length() - 1) : ret;
                    } else {
                        //cut the last subfolder
                        return ret.substring(0, lastSlash);
                    }
                }
                int endPosition = Math.min(lastQuestionMark, lastCross);
                endPosition = (endPosition == -1 && lastQuestionMark == -1) ? lastCross : lastQuestionMark;
                endPosition = (endPosition - 1 == lastSlash) ? endPosition - 1 : endPosition;
                return ret.substring(0, endPosition);
            case ONLY_DOMAIN:
                int protocolSlashPosition = ret.indexOf("//");
                int firstSlash = ret.indexOf('/', protocolSlashPosition + 2);
                return ret.substring(protocolSlashPosition + 2, (firstSlash <= protocolSlashPosition + 2) ? ret.length() - 1 : firstSlash);
            default:
                throw new IllegalArgumentException("For " + uriShortcutMode + " there is no handling defined in convertURI() until yet!");
        }
    }
}
