package com.chrislomeli.eventactivity.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventHandlerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void eventHandler() {
        // in: ServerRequest -- inpu payload variations
        // call event Service
        // out Mono<ServerResponse>
    }

    @Test
    void eventService() {
        // In : String json
        // call KafkaStreamWriter writer.write() --> boolean (Store Kafka)
        //
    }
}