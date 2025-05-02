package ru.onliver.translation_streamer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.onliver.translation_streamer.enums.KafkaTranslationEventType;

@Data
@AllArgsConstructor
public class TranslationEvent {
    @NonNull
    private KafkaTranslationEventType eventType;

    @NonNull
    private String roomName;
}
