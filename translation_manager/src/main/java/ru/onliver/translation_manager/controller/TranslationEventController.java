package ru.onliver.translation_manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import ru.onliver.translation_manager.enums.KafkaTranslationEventType;
import ru.onliver.translation_manager.model.TranslationEvent;
import ru.onliver.translation_manager.service.TranslationService;
import ru.onliver.translation_manager.util.KafkaProducer;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TranslationEventController {

    final TranslationService translationService;
    final KafkaProducer kafkaProducer;
    
    @KafkaListener(topics = "translation-events", groupId = "${spring.kafka.consumer.group-id}")
    public void listenRoomEvents(TranslationEvent event) {
        log.info("Получено событие трансляции: {}", event);

        switch (event.getEventType()) {
            case KafkaTranslationEventType.TRANSLATION_STARTED:
                break;
            case KafkaTranslationEventType.TRANSLATION_ENDED_EMERGENCY:
                translationService.cleanTranslation(event.getRoomName());
                kafkaProducer.send("translation-events", new TranslationEvent(KafkaTranslationEventType.TRANSLATION_FINISHED_ABORTED, event.getRoomName()));
                break;
            case KafkaTranslationEventType.TRANSLATION_ENDED_PLANNED:
                translationService.cleanTranslation(event.getRoomName());
                kafkaProducer.send("translation-events", new TranslationEvent(KafkaTranslationEventType.TRANSLATION_FINISHED_PLANNED, event.getRoomName()));
                break;
            case KafkaTranslationEventType.TRANSLATION_ENDED_MANUAL:
                kafkaProducer.send("translation-events", new TranslationEvent(KafkaTranslationEventType.TRANSLATION_FINISHED_ABORTED, event.getRoomName()));
                break;
            case KafkaTranslationEventType.TRANSLATION_FINISHED_ABORTED:
                break;
            case KafkaTranslationEventType.TRANSLATION_FINISHED_PLANNED:
                break;
            default:
                log.warn("Неизвестный тип события: {}", event.getEventType());
        }
    }


} 