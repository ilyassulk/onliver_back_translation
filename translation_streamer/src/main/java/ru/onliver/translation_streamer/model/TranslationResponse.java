package ru.onliver.translation_streamer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslationResponse {
    String roomName;
    String streamerIP;
}
