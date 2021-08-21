package com.chrislomeli.eventactivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import wiremock.org.eclipse.jetty.http.HttpHeader;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

// https://github.com/spring-cloud/spring-cloud-contract/tree/main/spring-cloud-contract-wiremock
// http://localhost:5000/email/v2/event
/*
  {
    "email": "example@test.com",
    "timestamp": 1626572022,
    "smtp-id": "\u003c14c5d75ce93.dfd.64b469@ismtpd-555\u003e",
    "event": "deferred",
    "category": [
      "cat facts"
    ],
    "sg_event_id": "GxuOAe0JYDkiYPs_gD1QEA==",
    "sg_message_id": "14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0",
    "response": "400 try again later",
    "attempt": "5"

 */
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@AutoConfigureWebFlux
@ComponentScan
@WebFluxTest(
        controllers = {
                SendgridApplication.class
        })
class RouterFunctionIT {

    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void happyRouterCall() throws JsonProcessingException {
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<>() {
        };

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("email", "cheese@gmail.com");
        map.put("timestamp", 1626572022L);
        list.add(map);

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(list);

        // Post
        var reponse = this.webTestClient
                .post()
                .uri("/events")
                .body(Mono.just(body), String.class)
                .header(HttpHeader.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString())
                .header(HttpHeader.ACCEPT.toString(), MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK_200)
                .returnResult(Void.class);
    }
    // maybe return partition, offset ???

    // Check for existence



}