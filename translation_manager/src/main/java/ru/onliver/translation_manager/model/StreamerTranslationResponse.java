package ru.onliver.translation_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamerTranslationResponse {
    String roomName;
    String streamerIP;
}
