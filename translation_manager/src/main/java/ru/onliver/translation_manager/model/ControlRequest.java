package ru.onliver.translation_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель запроса для управления трансляцией (пауза, воспроизведение, перемотка)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlRequest {
    private String roomName;
    private CommandType command;
    private Long seekTime;

    public enum CommandType {
        PAUSE,
        PLAY,
        SEEK,
        STOP
    }
} 