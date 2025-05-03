package ru.onliver.translation_streamer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Модель ответа на запрос трансляции.
 * Содержит информацию о созданной трансляции, включая имя комнаты и IP контейнера.
 */
@Data
@AllArgsConstructor
public class TranslationResponse {
    String roomName;
    String streamerIP;
}
