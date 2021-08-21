package com.chrislomeli.sendgridmail.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.config.ConfigurationManager;
import com.chrislomeli.sendgridmail.configuration.DeliverSendGridConfiguration;
import com.chrislomeli.sendgridmail.utils.UserNotificationFactory;
import com.chrislomeli.notificationmodels.datatypes.ErrorType;
import com.chrislomeli.notificationmodels.datatypes.Status;
import com.chrislomeli.notificationmodels.datatypes.Step;
import com.chrislomeli.notificationmodels.datatypes.Vendor;
import com.chrislomeli.notificationmodels.marshaller.JacksonObjectMapper;
import com.chrislomeli.notificationmodels.models.MessageHandlerResult;
import com.chrislomeli.notificationmodels.models.NotificationType;
import com.chrislomeli.notificationmodels.models.ObservableRequest;
import com.chrislomeli.notificationmodels.models.UserNotificationRequest;
import com.sendgrid.Request;
import com.sendgrid.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

//@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageProcessorTestMock {

    static ObjectMapper objectMapper = new JacksonObjectMapper().defaultObjectMapper();

    @Mock
    SendGridClient sendGridClient;

    @Autowired
    DeliverSendGridConfiguration deliverSendGridConfiguration;
    @Autowired
    SendGridMailAdapter sendGridMailAdapter;

    @InjectMocks
    SendGridMessageHandler transporter;

    @BeforeEach
    public void setUp() throws Exception {
        ConfigurationManager.loadCascadedPropertiesFromResources("sample");
    }

    @Test
    public void test_messageProcessor_201() throws Exception {

        // expected values
        int httpCode = 201;
        ErrorType expectedErrorType = ErrorType.NONE; // let the runner find it by http code
        Status expectedStatus = Status.SUCCESS;
        boolean expectedRetryable = false;
        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .build();

        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_500() throws Exception {
        // expected values
        int httpCode = 500;
        ErrorType expectedErrorType = ErrorType.SERVICE_ERROR; // let the runner find it by http code
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = true;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_501() throws Exception {
        // expected values
        int httpCode = 501;
        ErrorType expectedErrorType = ErrorType.SERVICE_ERROR; // let the runner find it by http code
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_502() throws Exception {
        // expected values
        int httpCode = 502;
        ErrorType expectedErrorType = ErrorType.SERVICE_ERROR; // let the runner find it by http code
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = true;
        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_429() throws Exception {
        // expected values
        int httpCode = 429;
        ErrorType expectedErrorType = ErrorType.SERVICE_THROTTLING;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = true;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_400() throws Exception {
        // expected values
        int httpCode = 400;
        ErrorType expectedErrorType = ErrorType.BAD_DATA;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_bad_sender() throws Exception {
        // expected values
        // expected values
        int httpCode = 0;
        ErrorType expectedErrorType = ErrorType.BAD_DATA;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .senderAddress("chees@bag.com")
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_malformed_sender() throws Exception {
        // expected values
        // expected values
        int httpCode = 0;
        ErrorType expectedErrorType = ErrorType.BAD_DATA;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .senderAddress("test-sender.chrislomeli.com")
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_malformed_recipient() throws Exception {
        // expected values
        int httpCode = 0;
        ErrorType expectedErrorType = ErrorType.VALIDATION_FAILED;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .recipientAddress("cheese@@bagcom")
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_empty_body() throws Exception {
        int httpCode = 0;
        ErrorType expectedErrorType = ErrorType.VALIDATION_FAILED;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .body("")
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }
    @Test
    public void test_Send_empty_subject() throws Exception {
        // expected values
        int httpCode = 0;
        ErrorType expectedErrorType = ErrorType.VALIDATION_FAILED;
        Status expectedStatus = Status.FAILED;
        boolean expectedRetryable = false;

        NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;
        UserNotificationFactory userNotificationFactory = UserNotificationFactory.builder()
                .subject("")
                .build();
        MessageHandlerResult result = sendMocked(httpCode, expectedErrorType, expectedStatus, expectedRetryable, userNotificationFactory);
        assertNotNull(result);
    }

    public MessageHandlerResult sendMocked(int httpCode,
                                           ErrorType expectedErrorType,
                                           Status expectedStatus,
                                           boolean expectedRetryable,
                                           UserNotificationFactory userNotificationFactory) throws IOException {

        transporter = new SendGridMessageHandler(sendGridClient, deliverSendGridConfiguration, sendGridMailAdapter);

        // boiler plate
        UserNotificationRequest unr = userNotificationFactory.create();

        Mockito.doReturn(new Response(httpCode, "Mock Mock Mock", null))
                .when(sendGridClient).sendMail(any(Request.class), any(String.class));

        // send
        ObservableRequest observableRequest = new ObservableRequest(unr);
        MessageHandlerResult result = transporter.execute(observableRequest);

        Assertions.assertEquals(result.getHttpStatusCode(), httpCode);

        if (result.getStatus() == Status.SUCCESS) {
            Map<String, Object> observations = observableRequest.getObservations();
            SendGridMail sendgridMail = (SendGridMail) observableRequest.getObservation("sendgrid.mailer", "");
            String actual_senderAddress = sendgridMail.getFrom().getEmail();
            String actual_Subject = sendgridMail.getSubject();
            String actual_recipientAddress = sendgridMail.getPersonalization().get(0).getTos().get(0).getEmail();
            String actual_actualBody = sendgridMail.getContent().get(0).getValue();
            Map<String, String> actual_customArgs = sendgridMail.getCustomArgs();

            Assertions.assertAll("Successful request data",
                    () -> assertEquals(userNotificationFactory.getCpCode(), actual_customArgs.get("cpCode"), "cp_code check"),
                    () -> assertEquals(userNotificationFactory.getCommId(), actual_customArgs.get("commId"), "comm_id check"),
                    () -> assertEquals(userNotificationFactory.getThreadId(), actual_customArgs.get("threadId"), "thread check"),
                    () -> assertEquals(userNotificationFactory.getSendId(), actual_customArgs.get("sendId"), "send_id check"),
                    () -> assertEquals(userNotificationFactory.getSenderAddress(), actual_senderAddress, "sender address check"),
                    () -> assertEquals(userNotificationFactory.getSubject(), actual_Subject, "email subject check"),
                    () -> assertEquals(userNotificationFactory.getRecipientAddress(), actual_recipientAddress, "recipient address check"),
                    () -> assertEquals(userNotificationFactory.getBody(), actual_actualBody, "message body check")
            );
        }
        assertNotNull(result);
        // assertions
        assertEquals(expectedErrorType, result.getErrorType(), "errorType check");
        assertEquals(expectedStatus, result.getStatus(), "status check");
        assertEquals(expectedRetryable, result.isRetryable(), "retryable check");

        // loopback assertions results
        Assertions.assertAll("Standard Asserts",
                () -> assertNotNull(result, "result is not null"),
                () -> assertEquals(Vendor.SENDGRID, result.getVendor(), "Vendor check"),
                () -> assertEquals(Step.TRANSPORT, result.getStep(), "Step check"),
                () -> assertEquals(5, result.getOrdinal(), "ordinal check")
        );

        return result;
    }

}