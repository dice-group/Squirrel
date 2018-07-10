package org.aksw.simba.squirrel.data.uri.info;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.CrawleableUriFactoryImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class URIReferencesUtilsTest {

    //DUMMY DATA
    CrawleableUriFactoryImpl factory = new CrawleableUriFactoryImpl();
    private final CrawleableUri mainURI1 = factory.create("https://www.philippheinisch.de/");
    private final CrawleableUri mainURI2 = factory.create("https://www.philippheinisch.de/aboutMe.html");
    private final CrawleableUri mainURI2_extend = factory.create("https://www.philippheinisch.de/aboutMe.html#me?p=1");
    private final List<CrawleableUri> foundURI1 = Collections.singletonList(factory.create("https://www.philippheinisch.de/projects.html"));
    private final List<CrawleableUri> foundURI2 = Collections.singletonList(factory.create("https://www.philippheinisch.de/multi/index.php"));

    @Test
    public void mergeLists() {
        URIReferencesUtils utils = new URIReferencesUtils(URIShortcutMode.TOTAL_URI);
        assertTrue("Merging two null's should return an empty list", utils.mergeLists(null, null).isEmpty());
        List<String> foundURI1String = foundURI1.stream().map(e -> e.getUri().toString()).collect(Collectors.toList());
        List<String> foundURI2String = foundURI2.stream().map(e -> e.getUri().toString()).collect(Collectors.toList());
        assertEquals("Merging nothing to something should return the same", foundURI1String, utils.mergeLists(foundURI1String, Collections.EMPTY_LIST));

        assertTrue("Simple merging of 2 lists", Arrays.equals(new String[]{"https://www.philippheinisch.de/projects.html", "https://www.philippheinisch.de/multi/index.php"}, utils.mergeLists(foundURI1String, foundURI2).toArray(new String[2])));
        assertTrue("Redundant merging of 2 lists", Arrays.equals(new String[]{"https://www.philippheinisch.de/projects.html"}, utils.mergeLists(foundURI1String, foundURI1).toArray(new String[1])));
    }

    @Test
    public void convertURI() {
        URIReferencesUtils utils;
        utils = new URIReferencesUtils(URIShortcutMode.TOTAL_URI);
        assertEquals("TOTAL_URI_TEST", "https://www.philippheinisch.de/aboutMe.html#me?p=1", utils.convertURI(mainURI2_extend));

        utils = new URIReferencesUtils(URIShortcutMode.NAMESPACE);
        assertEquals("NAMESPACE_TEST", "https://www.philippheinisch.de", utils.convertURI(mainURI2));
        assertEquals("NAMESPACE_TEST", "https://www.philippheinisch.de/aboutMe.html#me", utils.convertURI(mainURI2_extend));

        utils = new URIReferencesUtils(URIShortcutMode.ONLY_DOMAIN);
        assertEquals("ONLY_DOMAIN_TEST", "www.philippheinisch.de", utils.convertURI(mainURI1));
        assertEquals("ONLY_DOMAIN_TEST", "www.philippheinisch.de", utils.convertURI(mainURI2));
        assertEquals("ONLY_DOMAIN_TEST", "www.philippheinisch.de", utils.convertURI(mainURI2_extend));
    }
}
