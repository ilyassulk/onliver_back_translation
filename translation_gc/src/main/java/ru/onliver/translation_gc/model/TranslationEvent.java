package ru.onliver.translation_gc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.onliver.translation_gc.enums.KafkaTranslationEventType;

/**
 * Модель события трансляции для отправки в Kafka.
 * Содержит тип события и название комнаты, где происходит трансляция.
 */
@Data
@AllArgsConstructor
public class TranslationEvent {
    @NonNull
    private KafkaTranslationEventType eventType;

    @NonNull
    private String roomName;
}
