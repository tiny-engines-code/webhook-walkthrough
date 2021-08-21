package com.chrislomeli.sendgridmail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.chrislomeli"})
@Slf4j
public class SendgridApplication  {
    public static void main(String[] args) {
        SpringApplication.run(SendgridApplication.class, args);
    }
}
