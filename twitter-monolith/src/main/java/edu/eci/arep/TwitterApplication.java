package edu.eci.arep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for the Twitter-like monolithic Spring Boot application.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
@SpringBootApplication
public class TwitterApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to the JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(TwitterApplication.class, args);
    }
}
