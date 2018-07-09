package com.squirrel;

import com.squirrel.rabbit.RabbitMQListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The starting point for the Web-Service
 *
 * @author Philipp Heinisch
 */
@SpringBootApplication
public class Application {

    public static RabbitMQListener listenerThread;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        listenerThread = new RabbitMQListener();
        listenerThread.run();
    }
}
