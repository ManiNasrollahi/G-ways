package com.Gways;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Log
public class GatewayApplication {

    static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterStart() {
        log.info("G-WAYS application started.");
        log.info("Dashboard URL: http://localhost:8080/login.xhtml ");
        log.info("Default User: admin");
        log.info("Default password: admin");
    }

}
