package ru.onliver.translation_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "translation")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Translation {
    @Id
    String id;
    String roomName;
    String contentId;
    int status;
    String streamerIP;

    public Translation(String roomName, String contentId, int status, String streamerIP) {
        this.roomName = roomName;
        this.contentId = contentId;
        this.status = status;
        this.streamerIP = streamerIP;
    }
}
