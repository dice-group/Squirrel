package com.example;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping(value = {"/", "/home"}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String index() {
        return "<head>" +
            "<title>Philipp and Waleed presents</title>" +
            "</head>" +
            "<body>" +
            "<h1>Welcome :)</h1>" +
            "</body>";
    }
}
