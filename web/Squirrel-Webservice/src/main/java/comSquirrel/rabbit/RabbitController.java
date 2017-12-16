package comSquirrel.rabbit;

import comSquirrel.rabbitExchange.SquirrelWebObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RabbitController {

    private List<SquirrelWebObject> dataQueue = new ArrayList<>();

    @RabbitListener(queues = "WebRabbit")
    public void receivedMessage(SquirrelWebObject data) {
        dataQueue.add(data);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/observer", produces = MediaType.APPLICATION_JSON_VALUE)
    public SquirrelWebObject observeFrontier() {
        if (dataQueue.isEmpty())
            return new SquirrelWebObject();
        return dataQueue.get(dataQueue.size()-1);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/observer/stat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String observeFrontierStat(@RequestParam(value="prop", defaultValue="World") String property) {
        StringBuilder ret = new StringBuilder();
        if (property.equals("ls")) {
            dataQueue.forEach(c -> ret.append(c.toString()));
        } else if (property.equals("lsc")) {
            ret.append("You have " + dataQueue.size() + " SquirrelWebObjects");
        } else {
            ret.append("Please set another prop param");
        }

        return ret.toString();
    }
}
