package ru.onliver.translation_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.onliver.room_manager.enums.KafkaRoomEventType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomEvent {
    String eventType;
    String roomName;

    public static RoomEvent buildRoomStartedEvent(String roomName) {
        return new RoomEvent(KafkaRoomEventType.ROOM_STARTED.getName(), roomName);
    }

    public static RoomEvent buildRoomFinishedEvent(String roomName) {
        return new RoomEvent(KafkaRoomEventType.ROOM_FINISHED.getName(), roomName);
    }
}
