package com.chrislomeli.eventactivity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.chrislomeli"})
@EnableAsync
@Slf4j
public class EventActivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventActivityApplication.class, args);
    }
}
