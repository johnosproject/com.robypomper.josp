package com.robypomper.josp.jcp;

import com.robypomper.java.JavaSSLIgnoreChecks;
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

    private static final boolean DISABLE_SSL_CHECKS = true;

    public static void main(String[] args) {
        if (DISABLE_SSL_CHECKS)
            try {
                JavaSSLIgnoreChecks.disableSSLChecks(JavaSSLIgnoreChecks.LOCALHOST);
            } catch (JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException e) {
                System.out.println("Can't disable SSL checks for localhost communications, exit.");
                System.exit(-1);
            }

        SpringApplication.run(ServiceApplication.class, args);
    }

}
