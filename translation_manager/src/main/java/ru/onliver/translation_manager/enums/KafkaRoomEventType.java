package ru.onliver.translation_manager.enums;

import java.util.Arrays;

public enum KafkaRoomEventType {
    ROOM_STARTED("room_started"),
    ROOM_FINISHED("room_finished");

    String name;

    KafkaRoomEventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static KafkaRoomEventType fromString(String name) {
        return Arrays.stream(KafkaRoomEventType.values())
                .filter(eventType -> eventType.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid event type: " + name));
    }
}
