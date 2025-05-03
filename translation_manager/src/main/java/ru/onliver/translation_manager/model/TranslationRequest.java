package ru.onliver.translation_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель запроса на создание трансляции
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequest {
    private String roomName;
    private String contentURL;
} 