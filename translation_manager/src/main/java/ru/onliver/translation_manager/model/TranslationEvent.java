package ru.onliver.translation_manager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.onliver.translation_manager.enums.KafkaTranslationEventType;

/**
 * Модель для событий, связанных с трансляциями, передаваемых через Kafka
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationEvent {
    @NonNull
    @JsonProperty("eventType")
    private KafkaTranslationEventType eventType;

    @NonNull
    @JsonProperty("roomName")
    private String roomName;
}
