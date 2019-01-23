package com.squirrel.rabbit;

import com.SquirrelWebObject;
import com.graph.VisualisationGraph;
import com.squirrel.Application;
import com.squirrel.Utilities.HTMLReader;
import com.squirrel.Utilities.TemplateHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Listener for the RabbitMQ - receives and organize the {@link SquirrelWebObject}s
 *
 * @author Philipp Heinisch
 */
@SuppressWarnings("unused")
@RestController
public class RabbitController {

    @SuppressWarnings("all")
    @RequestMapping(method = RequestMethod.GET, path = "/observer", produces = MediaType.APPLICATION_JSON_VALUE)
    public SquirrelWebObject observeFrontier(@RequestParam(value = "id", defaultValue = "n/a") String property, @RequestParam(value = "percent", defaultValue = "false") String percent) {
        SquirrelWebObject o;
        try {
        	int id = Boolean.parseBoolean(percent) ? (int) ((Integer.parseInt(property) / 100f) * Application.listenerThread.countSquirrelWebObjects()) : Integer.parseInt(property);
            o = Application.listenerThread.getSquirrel(id);
        } catch (NumberFormatException e) {
            o = Application.listenerThread.getSquirrel();
        }

        if (o == null)
            return new SquirrelWebObject();
        return o;
    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.GET, path = "/observer/html", produces = MediaType.TEXT_HTML_VALUE)
    public String observerFrontierHTML(@RequestParam(value = "id", defaultValue = "n/a") String property, @RequestParam(value = "percent", defaultValue = "false") String percent) {
        SquirrelWebObject o = observeFrontier(property, percent);

        Map<String, List<String>> stringListMap = new HashMap<>();
        stringListMap.put("numberPendingURIs", Collections.singletonList(Integer.toString(o.getCountOfPendingURIs())));
        stringListMap.put("numberCrawledURIs", Collections.singletonList(Integer.toString(o.getCountOfCrawledURIs())));
        stringListMap.put("numberWorker", Collections.singletonList(Integer.toString(o.getCountOfWorker())));
        stringListMap.put("numberDeadWorker", Collections.singletonList(Integer.toString(o.getCountOfDeadWorker())));
        stringListMap.put("pendingURIs", o.getPendingURIs());
        stringListMap.put("nextCrawledURIs", o.getNextCrawledURIs());
        List<String> IPURImap = new ArrayList<>(o.getIpStringListMap().size());
        o.getIpStringListMap().forEach((k, v) -> {
            StringBuilder vString = new StringBuilder(": ");
            v.forEach(s -> vString.append(s).append(", "));
            IPURImap.add(k + vString.substring(0, vString.length() - 2));
        });
        stringListMap.put("IPURImap", IPURImap);

        return TemplateHelper.replace(HTMLReader.getText("./WEB-INF/pages/_index.html"), stringListMap);
    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.GET, path = "/observer/stat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String observeFrontierStat(@RequestParam(value = "prop", defaultValue = "help") String property) {
        StringBuilder ret = new StringBuilder();
        switch (property) {
            case "ls":
                for (int i = 0; i < Application.listenerThread.countSquirrelWebObjects(); i++) {
                    ret.append(Application.listenerThread.getSquirrel(i).toString()).append(System.lineSeparator());
                }
                break;
            case "lsc":
                ret.append(Application.listenerThread.countSquirrelWebObjects()).append(" SquirrelWebObjects are in the list");
                break;
            default:
                ret.append("Please set another prop param: ").append(System.lineSeparator()).append("- ls").append(System.lineSeparator()).append("- lsc");
                break;
        }

        return ret.toString();
    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.GET, path = "/observer/crawledGraph", produces = MediaType.APPLICATION_JSON_VALUE)
    public VisualisationGraph observeCrawledGraph(@RequestParam(value = "id", defaultValue = "n/a") String property) {
        VisualisationGraph graph;
        try {
            graph = Application.listenerThread.getCrawledGraph(Integer.parseInt(property));
        } catch (NumberFormatException e) {
            graph = Application.listenerThread.getCrawledGraph();
        }

        if (graph == null)
            return new VisualisationGraph();

        return graph;
    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.POST, path = "/observer/push", produces = MediaType.TEXT_PLAIN_VALUE)
    public String pushURI(@RequestParam(value = "uri", defaultValue = "") String uri) {
        //ERROR CATCHING
        if (uri == null) {
            return "Forwarding error. Try it again!";
        }
        uri = uri.trim().toLowerCase();
        if (uri.equals("")) {
            return "Please enter something!";
        }
        try {
            URI uriObject = new URI(uri);
            uri = (uriObject.getScheme() == null) ? "http://" + uri : uri;
        } catch (URISyntaxException e) {
            return "Your input " + e.getInput() + " is not a URI! Syntax error: " + e.getReason();
        }

        //PROCEEDING
        if (Application.listenerThread.publishURI(uri)) {
            return "Succeeded with forwarding the URI " + uri + " to the queue to the Frontier! Maybe the Frontier denies adding the URI to the pending URI list, so in cases of doubt pay attention to the frontier LOGGING or contact the developer";
        } else {
            return "Failed to forward the URI " + uri + " to the =rabbit=> frontier. Try it (later) again!";
        }
    }
}
