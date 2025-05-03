package ru.onliver.translation_streamer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Модель запроса на управление трансляцией.
 * Содержит команды для управления трансляцией, такие как пауза, воспроизведение, перемотка.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlRequest {
    private String roomName;
    private CommandType command;
    private Long seekTime; // Используется только для команды SEEK

    public enum CommandType {
        PAUSE,
        PLAY,
        SEEK,
        STOP
    }
} 