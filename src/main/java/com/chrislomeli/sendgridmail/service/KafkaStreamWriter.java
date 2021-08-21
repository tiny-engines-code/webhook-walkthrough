package com.chrislomeli.sendgridmail.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class KafkaStreamWriter   {

    private final StreamBridge stream;

    // Store your topic/binding name as the supplier name as follows
    private static final String TOPIC_NAME = "event_activity";

    public KafkaStreamWriter(StreamBridge stream) {
        this.stream = stream;
    }

    public void write(List<Map<String,Object>> eventActivity) {
        if ( stream.send(TOPIC_NAME, eventActivity) ) {
            log.debug("Sent: {}", eventActivity);
        } else {
            log.error("Failed {}", eventActivity);
        }
     }

}