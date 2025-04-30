package ru.onliver.translation_manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.onliver.translation_manager.model.Translation;

public interface TranslationRepository extends MongoRepository<Translation, String> {
    Translation findByRoomName(String roomName);
    void deleteByRoomName(String roomName);
}
