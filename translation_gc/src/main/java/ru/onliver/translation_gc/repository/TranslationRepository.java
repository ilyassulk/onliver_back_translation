package ru.onliver.translation_gc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.onliver.translation_gc.model.Translation;

/**
 * Интерфейс репозитория MongoDB для выполнения операций CRUD с объектами Translation.
 * Расширяет MongoRepository для работы с коллекцией трансляций.
 */
public interface TranslationRepository extends MongoRepository<Translation, String> { }
