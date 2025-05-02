package ru.onliver.translation_manager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.onliver.translation_manager.enums.KafkaTranslationEventType;

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
