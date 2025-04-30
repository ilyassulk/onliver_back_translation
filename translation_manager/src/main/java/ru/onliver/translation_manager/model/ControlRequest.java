package ru.onliver.translation_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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