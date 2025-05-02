package ru.onliver.translation_manager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.onliver.translation_manager.enums.KafkaRoomEventType;

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

    public static RoomEvent buildRoomStartedEvent(String roomName) {
        return new RoomEvent(KafkaRoomEventType.ROOM_STARTED.getName(), roomName);
    }

    public static RoomEvent buildRoomFinishedEvent(String roomName) {
        return new RoomEvent(KafkaRoomEventType.ROOM_FINISHED.getName(), roomName);
    }
}
