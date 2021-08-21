package com.chrislomeli.eventactivity;

import com.chrislomeli.eventactivity.controller.SendgridHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@SpringBootApplication(scanBasePackages = {"com.chrislomeli"})
@EnableAsync
@Slf4j
public class SendgridApplication  {

    public static void main(String[] args) {
        SpringApplication.run(SendgridApplication.class, args);
    }
}
