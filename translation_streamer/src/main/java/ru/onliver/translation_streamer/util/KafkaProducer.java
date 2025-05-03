package ru.onliver.translation_streamer.util;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Утилитарный класс для отправки сообщений в Kafka.
 * Обеспечивает отправку событий в топики Kafka.
 */
@Component
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Object payload) {
        kafkaTemplate.send(topic, payload)
                .whenComplete((meta, ex) -> {
                    if (ex != null) {
                        System.err.printf("Send error in %s: %s%n", topic, ex.getMessage());
                    } else {
                        System.out.printf("Message sent %s, offset=%d%n",
                                topic, meta.getRecordMetadata().offset());
                    }
                });
    }
}