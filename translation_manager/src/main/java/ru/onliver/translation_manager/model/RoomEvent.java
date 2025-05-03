package ru.onliver.translation_manager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.onliver.translation_manager.enums.KafkaRoomEventType;

/**
 * Модель для событий комнаты, обрабатываемых через Kafka
 */
@Data
@NoArgsConstructor
public class RoomEvent {
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("roomName")
    private String roomName;
    
    public RoomEvent(String eventType, String roomName) {
        this.eventType = eventType;
        this.roomName = roomName;
    }
}
