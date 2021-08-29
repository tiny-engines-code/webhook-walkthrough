package com.chrislomeli.eventactivity.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaWriter  {

  @Value("${topic.name:sendgrid_events}")
  private String topic;

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public KafkaWriter(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

/**
 * KafkaCallback
 *  Our callback from the Kafka Producer */
  static class KafkaCallback implements ListenableFutureCallback<SendResult<String, Object>> {
    @Override
    public void onFailure(Throwable ex) {
      log.error("FOOBAR-DLQ: {}", ex.getLocalizedMessage());
    }

    @Override
    public void onSuccess(SendResult<String, Object> result) {
      if (result == null ) return;
      RecordMetadata meta = result.getRecordMetadata();
      log.debug("Delivered {}<--[{}] at partition={}, offset={}", meta.topic(), result.toString(), meta.partition(), meta.offset());
    }
  }

  /*
   * Write to the Spring kafka producer
   *  the real producer is wrapped by our KafkaTemplate bean
   *  Our callback from the Kafka Producer */
  public void write(Object Object) {
    try {
      var future = this.kafkaTemplate.send(this.topic, Object);
      future.addCallback(new KafkaCallback());

    } catch (Exception e) {
    }
  }
}
