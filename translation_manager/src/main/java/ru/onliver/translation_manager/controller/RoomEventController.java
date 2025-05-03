package ru.onliver.translation_manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import ru.onliver.translation_manager.enums.KafkaRoomEventType;
import ru.onliver.translation_manager.model.RoomEvent;
import ru.onliver.translation_manager.service.TranslationService;

/**
 * Контроллер для обработки событий комнат через Kafka
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomEventController {

    final TranslationService translationService;
    
    @KafkaListener(topics = "room-events", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "roomKafkaListenerContainerFactory")
    public void listenRoomEvents(RoomEvent event) {
        log.info("Получено событие комнаты: {}", event);

        try {
            KafkaRoomEventType eventType = KafkaRoomEventType.fromString(event.getEventType());
            
            switch (eventType) {
                case ROOM_STARTED:
                    break;
                case ROOM_FINISHED:
                    translationService.stopTranslation(event.getRoomName());
                    break;
                default:
                    log.warn("Неизвестный тип события: {}", event.getEventType());
            }
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при обработке события комнаты: {}", e.getMessage());
        }
    }


} 