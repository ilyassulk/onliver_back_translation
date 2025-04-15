package ru.onliver.translation_streamer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlCommand {
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