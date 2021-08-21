package com.chrislomeli.sendgridmail.controller;

import com.chrislomeli.sendgridmail.service.KafkaStreamWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@Component
public class SendgridMethods {

    @Value("${sendgrid.batchsize.max:50}")
    int chunkSize;

    private final KafkaStreamWriter writer;

    static final  ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public SendgridMethods(KafkaStreamWriter writer) {
        this.writer = writer;
    }

    public void eventActivity(String jsonInput) throws JsonProcessingException {
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<>() {};
        List<Map<String, Object>> list = objectMapper.readValue(jsonInput, typeRef);

        // nice to leave the records in batches for performance, but make sure the batches are not huge
        AtomicInteger counter = new AtomicInteger();
        final Collection<List<Map<String, Object>>> partitionedList =
                list.stream().collect(Collectors.groupingBy(i -> counter.getAndIncrement() / chunkSize))
                        .values();
        partitionedList.forEach(writer::write);
        log.debug("records recieved {}, Chunksize {}, partitioned into {} lists", list.size(), chunkSize, partitionedList.size());

    }
}
