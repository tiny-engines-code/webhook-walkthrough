package com.chrislomeli.sendgridmail.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chrislomeli.notificationmodels.marshaller.JacksonObjectMapper;
import com.chrislomeli.notificationmodels.models.*;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Builder
@Data
public class UserNotificationFactory {

    @Builder.Default
    private String subject = "Mysubject";
    @Builder.Default
    private String body = "Lorem Ipsum";
    @Builder.Default
    private String senderName = "Nike at Test";
    @Builder.Default
    private String senderAddress = "chris.lomeli@chrislomeli.com";
    @Builder.Default
    private String recipientAddress = "chris.lomeli@chrislomeli.com";
    @Builder.Default
    private String cpCode = "cp-001";
    @Builder.Default
    private String commId = "comm-001";
    @Builder.Default
    private String sendId = "send-001";
    @Builder.Default
    private String threadId = "thread-001";
    @Builder.Default
    private boolean mock = false;
    @Builder.Default
    private MediaType mediaType = MediaType.ALL;
    @Builder.Default
    private NotificationType notificationType = NotificationType.GENERIC_CAMPAIGN;

    public UserNotificationRequest create() throws JsonProcessingException {
        ObjectMapper mapper = new JacksonObjectMapper().defaultObjectMapper();

        RenderInfo renderInfo = new RenderInfo();
        renderInfo.setSubject(subject);
        renderInfo.setRenderedOutput(body);
        String jsonRnder = mapper.writeValueAsString(renderInfo);

        Content content = new Content();
        content.setAdditionalProperty("email", jsonRnder);

        TestUtility testUtility = new TestUtility();
        if (!this.mediaType.equals(MediaType.ALL)) {
            Attachment attachment = new Attachment();

            Pair<String, String> filedata = testUtility.getAttachment(this.mediaType);
            if (filedata != null) {
                String attachmentBody = filedata.getRight();
                String attachmentFilename = filedata.getLeft();

                attachment.setContentType(mediaType.toString());
                attachment.setAttachment(attachmentBody);
                attachment.setLabel("ATTACHMENT-LABEL-1");
                attachment.setName(attachmentFilename);
                content.setAdditionalProperty(ConstantStrings.ATTACHMENT_KEY, mapper.writeValueAsString(attachment));
            }
        }

        content.setAdditionalProperty("commId", commId);
        content.setAdditionalProperty("cpCode", cpCode);
        content.setAdditionalProperty("senderName", senderName);
        content.setAdditionalProperty("senderEmail", senderAddress);
        if (this.mock) {
            content.setAdditionalProperty("sendgrid.mock-mode", true);
        }

        EmailAttributes emailAttributes = EmailAttributes.builder()
                .collectionGroupId("collection-group-1")
                .sendId(this.getSendId())
                .threadId(this.getThreadId())
                .build();

        ChannelAttributes ca = ChannelAttributes.builder()
                .email(emailAttributes)
                .build();

        return UserNotificationRequest.builder()
                .endTime(Instant.now().toString())
                .startTime(Instant.now().toString())
                .deliveryChannels(Collections.singleton(DeliveryChannel.EMAIL))
                .localeCountry("US")
                .content(content)
                .channelProperties(ca)
                .localeLanguage("en_US")
                .notificationType(notificationType)
                .recipientUserId("B9093092334")
                .recipientEmailAddress(recipientAddress)
                .senderUserId(sendId)
                .publishedTime(Instant.now().toString())
                .receivedTime(Instant.now().toString())
                .requestId(UUID.randomUUID().toString())
                .build();
    }
}
