package com.sitewhere.rdb;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Entry point class for Spring data JPA
 *
 * Simeon Chen
 */
@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Bean
    public ExitCodeGenerator exitCodeGenerator() {
        return () -> 42;
    }

    @Bean
    public CommandLineRunner demo() {
        return (args) -> {
        };
    }
}