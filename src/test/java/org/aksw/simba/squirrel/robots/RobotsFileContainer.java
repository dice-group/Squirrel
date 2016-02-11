package org.aksw.simba.squirrel.robots;

import java.io.IOException;
import java.io.OutputStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotsFileContainer implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(RobotsFileContainer.class);

    public RobotsFileContainer(String robotsFileContent) {
        this.robotsFileContent = robotsFileContent;
    }

    private String robotsFileContent;
    private Throwable throwable;

    @Override
    public void handle(Request request, Response response) {
        if (request.getTarget().equals("/robots.txt")) {
            OutputStream out = null;
            try {
                byte data[] = robotsFileContent.getBytes("UTF-8");
                response.setCode(Status.OK.code);
                response.setValue("Content-Type", "text/plain;charset=utf-8");
                response.setContentLength(data.length);
                out = response.getOutputStream();
                out.write(data);
            } catch (Exception e) {
                LOGGER.error("Got exception.", e);
                if (throwable != null) {
                    throwable = e;
                }
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            LOGGER.error("Got a request for a different URL than the robots.txt: \"" + request.getAddress()
                    + "\". Creating exception object for this.");
            throwable = new IllegalAccessException(
                    "Got a request for a different URL than the robots.txt: \"" + request.getAddress() + "\".");
            response.setCode(Status.BAD_REQUEST.code);
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
            }
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
