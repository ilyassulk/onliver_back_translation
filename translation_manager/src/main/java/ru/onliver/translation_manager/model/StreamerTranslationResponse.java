package ru.onliver.translation_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Модель ответа от стримера с информацией о трансляции
 */
@Data
@AllArgsConstructor
public class StreamerTranslationResponse {
    String roomName;
    String streamerIP;
}
