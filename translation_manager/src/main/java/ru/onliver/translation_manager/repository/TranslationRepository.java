package ru.onliver.translation_manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.onliver.translation_manager.model.Translation;

import java.util.Optional;

public interface TranslationRepository extends MongoRepository<Translation, String> {
    Optional<Translation> findByRoomName(String roomName);
    void deleteByRoomName(String roomName);
}
