package com.robypomper.josp.jcp.apis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JCP API's service main class.
 *
 * This class start a Spring Boot application that provide JCP API services.
 */
@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
