package com.collectman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CollectmanApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollectmanApplication.class, args);
    }

}
