package com.chrislomeli.sendgridmail.service.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerConfiguration {

    @Value(value = "${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value(value = "${topic.security:false}")
    private String security;

    @Value(value = "${topic.client_secret:none}")
    private String client_secret;

    @Value(value = "${topic.client_id:none}")
    private String client_id;

    @Value(value = "${topic.token_url:none}")
    private String token_url;

    @Bean
    public ProducerFactory<String, Object> producerFactory() throws ClassNotFoundException {
        Map<String, Object> clientProperties = new HashMap<>();
        clientProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "appid");
        clientProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        clientProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        clientProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        clientProperties.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 10000);
        clientProperties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        clientProperties.put(ProducerConfig.RETRIES_CONFIG, 3);
        clientProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15000);
        clientProperties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        clientProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        clientProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384 * 4); // Batch up to 64K buffer in the queue.
        clientProperties.put(ProducerConfig.LINGER_MS_CONFIG, 100); // wait this long before de-queue regardless of queue size

        if ("true".equals(security)) {
            String jassConfig = "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required " +
                    String.format("oauth.token.endpoint.uri=\"%s\" ", token_url) +
                    String.format("oauth.client.id=\"%s\" ", client_id) +
                    String.format("oauth.client.secret=\"%s\" ;", client_secret);

            clientProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            clientProperties.put(SaslConfigs.SASL_MECHANISM, "OAUTHBEARER");
            clientProperties.put(SaslConfigs.SASL_JAAS_CONFIG, jassConfig);
            clientProperties.put(SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS, "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");
        }
        return new DefaultKafkaProducerFactory<>(clientProperties);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() throws ClassNotFoundException {
        return new KafkaTemplate<>(producerFactory());
    }


}
