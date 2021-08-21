package com.chrislomeli.sendgridmail.service;


import com.netflix.config.ConfigurationManager;
import com.chrislomeli.sendgridmail.configuration.DeliverSendGridConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SendGridConfigsTest {

    final DeliverSendGridConfiguration configuration = new DeliverSendGridConfiguration();

    @BeforeEach
    public void setUp() throws Exception {
        ConfigurationManager.loadPropertiesFromResources("sample.properties");
    }

    @Test
    public void test_Send_getValidSenders() throws Exception {
        Set<String> al = configuration.getValidSenders();
        assertNotNull(al);
    }
    @Test
    public void test_Send_getRecoverableCodes() throws Exception {
        Set<Integer> al = configuration.getRecoverableCodes();
        assertNotNull(al);
    }
    @Test
    public void test_Send_getSendGridMailAPIKeys() throws Exception {
        assertNotNull(configuration.getMailerAuth("generic:campaign@official.chrislomeli.com"));
        assertNotNull(configuration.getMailerAuth("generic:transactional@official.chrislomeli.com"));
        assertNotNull(configuration.getMailerAuth("generic:trigger@official.chrislomeli.com"));

        assertNotNull(configuration.getMailerAuth("generic:campaign@notifications.chrislomeli.com"));
        assertNotNull(configuration.getMailerAuth("generic:transactional@notifications.chrislomeli.com"));
        assertNotNull(configuration.getMailerAuth("generic:trigger@notifications.chrislomeli.com"));
    }

    @Test
    public void test_Send_getCustomTags() throws Exception {
        Set<String> al = configuration.getCustomTags();
        assertNotNull(al);
        assertTrue(al.contains("cpCode"));
        assertTrue(al.contains("commId"));
        assertTrue(al.contains("sendId"));
        assertTrue(al.contains("threadId"));
        assertFalse(al.contains("dummy"));
    }

    @Test
    public void test_Send_getSandboxMode() throws Exception {
        Boolean al = configuration.getSandboxMode();
        assertFalse(al);
    }

    @Test
    public void test_Send_getMockMode() throws Exception {
        Boolean al = configuration.getMockMode();
        assertFalse(al);
    }

}

