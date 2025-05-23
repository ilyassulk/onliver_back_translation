package ru.onliver.translation_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Модель данных для хранения информации о трансляции
 */
@Document(collection = "translation")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Translation {
    @Id
    String id;
    String roomName;
    String contentURL;
    String streamerIP;

    public Translation(String roomName, String contentURL, String streamerIP) {
        this.roomName = roomName;
        this.contentURL = contentURL;
        this.streamerIP = streamerIP;
    }
}