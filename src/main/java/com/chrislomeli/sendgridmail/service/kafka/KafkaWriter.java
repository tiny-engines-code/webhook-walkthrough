package com.chrislomeli.servicetemplate.writers.kafka;

import com.chrislomeli.servicetemplate.model.EventsResponse;
import com.chrislomeli.servicetemplate.service.Writer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaWriter implements Writer {

  @Value("${topic.name:events}")
  private String TOPIC;

  private final KafkaTemplate<String, EventsResponse> kafkaTemplate;

  public KafkaWriter(KafkaTemplate<String, EventsResponse> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  static class KafkaCallback implements ListenableFutureCallback<SendResult<String, EventsResponse>> {
    @Override
    public void onFailure(Throwable ex) {
      log.error("FFOBAR: {}", ex.getLocalizedMessage());
    }

    @Override
    public void onSuccess(SendResult<String, EventsResponse> result) {
      RecordMetadata meta = result.getRecordMetadata();
      log.debug("Delivered {}<--[{}] at partition={}, offset={}", meta.topic(), result.toString(), meta.partition(), meta.offset());
    }
  }

  @Override
  public EventsResponse write(EventsResponse eventsResponse) {

    try {
      var future = this.kafkaTemplate.send(this.TOPIC, eventsResponse);
      future.addCallback(new KafkaCallback());

      return eventsResponse;
    } catch (Exception e) {
    }

    return eventsResponse;
  }
}
