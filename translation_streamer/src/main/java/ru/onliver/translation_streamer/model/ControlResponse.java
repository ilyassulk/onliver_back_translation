package ru.onliver.translation_streamer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Модель ответа на запрос управления трансляцией.
 * Содержит результат выполнения команды управления.
 */
@Data
@AllArgsConstructor
public class ControlResponse {
    String roomName;
}
