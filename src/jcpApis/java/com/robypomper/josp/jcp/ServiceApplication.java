package com.robypomper.josp.jcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * JCP API's service main class.
 *
 * This class start a Spring Boot application that provide JCP API services.
 */
@SpringBootApplication
@Configuration
@ComponentScan
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
