package ru.onliver.translation_streamer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Модель запроса на начало трансляции.
 * Содержит параметры для запуска новой трансляции, включая имя комнаты и URL контента.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequest {
    private String roomName;
    private String contentURL;
} 