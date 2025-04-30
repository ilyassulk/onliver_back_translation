package ru.onliver.translation_manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import ru.onliver.translation_manager.enums.KafkaRoomEventType;
import ru.onliver.translation_manager.model.RoomEvent;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomEventController {
    
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenRoomEvents(RoomEvent roomEvent) {
        log.info("Получено событие комнаты: {}", roomEvent);

        switch (KafkaRoomEventType.fromString(roomEvent.getEventType())) {
            case KafkaRoomEventType.ROOM_STARTED:
                break;
            case KafkaRoomEventType.ROOM_FINISHED:

                break;
            default:
                log.warn("Неизвестный тип события: {}", roomEvent.getEventType());
        }
    }


} 