package ru.onliver.translation_gc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.onliver.translation_gc.model.Translation;

public interface TranslationRepository extends MongoRepository<Translation, String> { }
