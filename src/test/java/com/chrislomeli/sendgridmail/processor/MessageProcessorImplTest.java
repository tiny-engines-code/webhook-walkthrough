package com.chrislomeli.sendgridmail.processor;


import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.config.ConfigurationManager;
import com.chrislomeli.sendgridmail.utils.UserNotificationFactory;
import com.chrislomeli.notificationmodels.datatypes.ErrorType;
import com.chrislomeli.notificationmodels.datatypes.Status;
import com.chrislomeli.notificationmodels.marshaller.JacksonObjectMapper;
import com.chrislomeli.notificationmodels.models.NotificationType;
import com.chrislomeli.notificationmodels.models.UserNotificationRequest;
import com.chrislomeli.notificationpublisher.service.NotificationPublisher;
import com.chrislomeli.notificationstreamsqs.exception.NonRetryableException;
import com.chrislomeli.notificationstreamsqs.exception.RetryableException;
import com.chrislomeli.transportercore.configuration.TransporterConfiguration;
import com.chrislomeli.transportercore.util.JsonUtil;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static com.chrislomeli.sendgridmail.ConstantStrings.RECEIVE_COUNT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageProcessorImplTest {

    static ObjectMapper objectMapper = new JacksonObjectMapper().defaultObjectMapper();

    @Autowired
    SendGridClient sendGridClient;

    @Autowired
    TransporterConfiguration transporterConfiguration;
    @Autowired
    SendGridMessageHandler sendGridMessageHandler;
    @Autowired
    NotificationPublisher notificationPublisher;

    @Autowired
    MessageProcessorImpl transporter;


    @Before
    public void setUp() throws Exception {
        ConfigurationManager.loadCascadedPropertiesFromResources("sample");
    }

    @Test
    public void test_messageProcessor_good() throws Exception {
        // expected values
        ErrorType expectedErrorType = ErrorType.NONE;
        Status expectedStatus = Status.SUCCESS;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .mock(true)
                .build();

        sendToProcessor( expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
    }
    @Test
    public void test_messageProcessor_empty_body() throws Exception {
        // expected values
        ErrorType expectedErrorType = ErrorType.VALIDATION_FAILED;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .mock(true)
                .body("")
                .build();

        sendToProcessor( expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
    }
    @Test
    public void test_messageProcessor_empty_subject() throws Exception {
        // expected values
        ErrorType expectedErrorType = ErrorType.VALIDATION_FAILED;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .mock(true)
                .subject("")
                .build();

        sendToProcessor( expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
    }
    @Test
    public void test_messageProcessor_email_not_whitelisted() throws Exception {
        // expected values
        ErrorType expectedErrorType = ErrorType.VALIDATION_FAILED;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .mock(true)
                .senderAddress("ishcapible@wonky.com") // not in the whitelist
                .build();

        sendToProcessor( expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
    }

    public void sendToProcessor(
                                           ErrorType expectedErrorType,
                                           Status expectedStatus,
                                           boolean expectedRetryable,
                                           UserNotificationFactory userNotificationFactory) throws IOException {
        // boiler plate
        UserNotificationRequest unr = userNotificationFactory.create();

        Message message = new Message()
                .withMessageId("id")
                .withBody(JsonUtil.writeCompressedJsonValue(objectMapper, unr))
                .addMessageAttributesEntry(RECEIVE_COUNT, new MessageAttributeValue().withDataType("int").withStringValue("3"));

        transporter = new MessageProcessorImpl(sendGridMessageHandler,notificationPublisher,transporterConfiguration);
        if (Status.SUCCESS.equals(expectedStatus))
            assertDoesNotThrow( () -> transporter.execute(message));
        else if (expectedRetryable )
            assertThrows(RetryableException.class, () -> transporter.execute(message));
        else
            assertThrows(NonRetryableException.class, () -> transporter.execute(message));
    }
}