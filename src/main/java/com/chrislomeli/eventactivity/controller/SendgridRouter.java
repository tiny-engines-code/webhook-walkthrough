package com.chrislomeli.eventactivity.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SendgridRouter {
    @Bean
    public RouterFunction<ServerResponse> route(SendgridHandler handler) {
        return RouterFunctions.route()
                .POST("/events", handler::eventHandler)
                .build();
    }
}