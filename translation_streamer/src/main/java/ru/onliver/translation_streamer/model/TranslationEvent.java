package ru.onliver.translation_streamer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.onliver.translation_streamer.enums.KafkaTranslationEventType;

/**
 * Модель события трансляции.
 * Представляет события, происходящие во время трансляции, для отправки через Kafka.
 */
@Data
@AllArgsConstructor
public class TranslationEvent {
    @NonNull
    private KafkaTranslationEventType eventType;

    @NonNull
    private String roomName;
}
