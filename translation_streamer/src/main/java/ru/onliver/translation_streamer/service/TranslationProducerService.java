package ru.onliver.translation_streamer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.onliver.translation_streamer.enums.KafkaTranslationEventType;
import ru.onliver.translation_streamer.model.TranslationEvent;
import ru.onliver.translation_streamer.util.KafkaProducer;

/**
 * Сервис для генерации и отправки событий перевода.
 * Производит сообщения и отправляет их в Kafka для обработки другими сервисами.
 */
@Slf4j
@Service
public class TranslationProducerService {
    private final KafkaProducer kafkaProducer;

    public TranslationProducerService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void publishTranslationEndManualEvent(String roomName) {
        this.publishTranslationEvent(new TranslationEvent(KafkaTranslationEventType.TRANSLATION_ENDED_MANUAL, roomName));
    }

    public void publishTranslationEndEmergencyEvent(String roomName) {
        this.publishTranslationEvent(new TranslationEvent(KafkaTranslationEventType.TRANSLATION_ENDED_EMERGENCY, roomName));
    }

    public void publishTranslationEndPlannedEvent(String roomName) {
        this.publishTranslationEvent(new TranslationEvent(KafkaTranslationEventType.TRANSLATION_ENDED_PLANNED, roomName));
    }

    private void publishTranslationEvent(TranslationEvent event) {
        try {
            kafkaProducer.send("translation-events", event);
            log.info("Kafka event {} sent for room {}", event.getEventType(), event.getRoomName());
        } catch (Exception ex) {
            log.error("Cannot publish Kafka event {} for room {}", event.getEventType(), event.getRoomName(), ex);
        }
    }
}
