package com.chrislomeli.sendgridmail.service;

import com.chrislomeli.sendgridmail.service.kafka.KafkaWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@Component
@EnableAsync
public class SendgridHandler {

    @Value("${sendgrid.batchsize.max:50}")
    int chunkSize;

    private final KafkaWriter writer;

    static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public SendgridHandler(KafkaWriter writer) {
        this.writer = writer;
    }

    @NotNull
    public Mono<ServerResponse> eventHandler(ServerRequest serverRequest) {
        return  serverRequest.bodyToMono(String.class)
                .doOnNext(this::eventService)
                .then(ServerResponse.ok().build());
    }

    @Async
    public void eventService(String json) {
        try {
            // a list of different schemas can come in, so handle as a list of maps
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<>() {};
            List<Map<String, Object>> list = objectMapper.readValue(json, typeRef);

            // nice to leave the records in batches for performance, but make sure the batches are not huge
            if (list.size() <= chunkSize) {
                // send it!
                writer.write(list);
            } else {
                // split into arrays of chunkSize
                AtomicInteger counter = new AtomicInteger();
                list.stream()
                        .collect(Collectors.groupingBy(i -> counter.getAndIncrement() / chunkSize))
                        .values()
                        .forEach(writer::write);
            }

        } catch (Exception ignore) {
            // todo - forward failures to re-queue
        }

    }


}
