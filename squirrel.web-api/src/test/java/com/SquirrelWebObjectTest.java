package com;

import com.graph.VisualisationGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SquirrelWebObjectTest {

    SquirrelWebObject o;
    List<String> pendingURIlist;
    Map<String, List<String>> IPMapList;

    @Before
    public void setUp() throws Exception {
        o = new SquirrelWebObject();
        pendingURIlist = new ArrayList<>(3);
        pendingURIlist.add("1. URI");
        pendingURIlist.add("https://philippheinisch.de");
        pendingURIlist.add("<http://dbPedia.org/ontology/>");
        o.setPendingURIs(pendingURIlist);

        IPMapList = new HashMap<>(2);
        IPMapList.put("1. Set", pendingURIlist);
        IPMapList.put("2. Set", pendingURIlist);

        o.setIPMapPendingURis(IPMapList);
    }

    @Test
    public void convertToByteStream() {
        byte[] stream = o.convertToByteStream();

        SquirrelWebObject cal = SquirrelWebObjectHelper.convertToObject(stream);

        assertEquals(o.getCountOfPendingURIs(), cal.getCountOfPendingURIs());
        assertEquals(o.getIpStringListMap().hashCode(), cal.getIpStringListMap().hashCode());
        assertEquals(o.getPendingURIs().get(0).hashCode(), cal.getPendingURIs().get(0).hashCode());
        assertEquals(o.getIpStringListMap().entrySet().stream().filter(w -> w.getKey().equals("2. Set")).findFirst().get().getValue().get(2).hashCode(), cal.getIpStringListMap().entrySet().stream().filter(w -> w.getKey().equals("2. Set")).findFirst().get().getValue().get(2).hashCode());
    }

    @Test
    public void equals() {
        SquirrelWebObject o1 = new SquirrelWebObject();
        SquirrelWebObject o2 = new SquirrelWebObject();

        assertTrue("2 new SquirrelWebObjects should be equal...", o1.equals(o2));

        ArrayList<String> pendingURIs = new ArrayList<>();
        pendingURIs.add("https://www.philippheinisch.de");
        pendingURIs.add("https://www.bibleserver.com/");
        try {
            o2.setPendingURIs(pendingURIs);
            o2.setIPMapPendingURis(Collections.singletonMap("1.1.1.1", pendingURIs));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        assertFalse("One of them got pending URIs --> not equal any more", o1.equals(o2));

        try {
            o1.setPendingURIs((List<String>) pendingURIs.clone());
            o1.setIPMapPendingURis(Collections.singletonMap("1.1.1.1", pendingURIs));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        assertTrue("Now the other SquirrelWebObjects got the same list --> equal again!", o1.equals(o2));

        o1.setGraph(new VisualisationGraph());
        o2.setGraph(new VisualisationGraph());

        assertTrue("SquirrelWebObjects with a similar graph should be the same", o1.equals(o2));

    }
}
