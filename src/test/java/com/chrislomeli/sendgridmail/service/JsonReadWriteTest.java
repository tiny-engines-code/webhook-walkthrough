package com.chrislomeli.sendgridmail.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chrislomeli.sendgridmail.utils.UserNotificationFactory;
import com.chrislomeli.notificationmodels.marshaller.JacksonObjectMapper;
import com.chrislomeli.notificationmodels.models.UserNotificationRequest;
import com.chrislomeli.notificationstreamsqs.exception.NonRetryableException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

public class JsonReadWriteTest {
    static ObjectMapper objectMapper = new JacksonObjectMapper().defaultObjectMapper();


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test_ReadWrite() throws Exception {
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .mediaType(MediaType.APPLICATION_JSON)
                .build();
        UserNotificationRequest unr = userNotificationFactory.create();

        String json = getTextPayload();
        String userNotificationMessage = objectToJson(objectMapper, unr);

        UserNotificationRequest unr2 = jsonToObject(objectMapper, userNotificationMessage, UserNotificationRequest.class);

        Assertions.assertNotNull(unr2);


    }

    public String getTextPayload() {

        String payload = null;
        try {
            URL p = this.getClass().getClassLoader().getResource("json/generic_campaign.json");
            if (p==null)
                return null;
            File i = new File(p.getPath());
            payload = new Scanner(i)
                    .useDelimiter("\\Z").next().trim();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return payload;
    }


    /**
     * Util wrapper method ObjectMapper's writeValueAsString method. This catches JsonProcessingException
     * and converts it into NonRetryableException. Marks it with error_level=critical log.
     * @param r ObjectMapper.
     * @param t object.
     * @param <R> R extends ObjectMapper.
     * @param <T> generic for object to be returned.
     * @return returns json String of object t or throws NonRetryableException.
     */
    public static <R extends ObjectMapper, T> String objectToJson(R r, T t) {
        try {
            return r.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw NonRetryableException.newBuilder().withExceptionMessage("Error deserializing object")
                    .withExceptionCause(e)
                    .withLogEntry(Pair.of("ClassName", t.getClass().getSimpleName()))
                    .asCriticalFailure()
                    .build();
        }
    }

    /**
     */
    public static <R extends ObjectMapper, S extends String, T> T jsonToObject(R r, S s, Class<T> t) {
        try {
            return r.readValue(s, t);
        } catch (JsonProcessingException e) {
            throw NonRetryableException.newBuilder().withExceptionMessage("Invalid JSON payload")
                    .withExceptionCause(e)
                    .withLogEntry(Pair.of("ClassName", t.getSimpleName()))
                    .withLogEntry(Pair.of("payload", s))
                    .asCriticalFailure()
                    .build();
        }
    }


}
