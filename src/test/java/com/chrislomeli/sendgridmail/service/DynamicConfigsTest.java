package com.chrislomeli.sendgridmail.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.chrislomeli.sendgridmail.configuration.DeliverSendGridConfiguration;
import com.chrislomeli.notificationmodels.marshaller.JacksonObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;


@SpringBootTest
public class DynamicConfigsTest {

    final DeliverSendGridConfiguration configuration = new DeliverSendGridConfiguration();
    ObjectMapper objectMapper = new JacksonObjectMapper().defaultObjectMapper();

    @BeforeEach
    public void setUp() throws Exception {
        ConfigurationManager.loadPropertiesFromResources("sample.properties");
    }

    @Test
    public void test_DynamicMapConversion() throws Exception {
        String  mapString = DynamicPropertyFactory.getInstance()
                .getStringProperty("sendgrid.mailer-keys", "{}").get();

        Map<String,String> mapValue = objectMapper.readValue(mapString, new TypeReference<Map<String, String>>() {});
        Assertions.assertNotNull(mapValue);


    }
}

