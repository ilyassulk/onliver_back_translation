package ru.onliver.translation_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения, отвечающий за запуск и инициализацию менеджера переводов
 */
@SpringBootApplication
public class TranslationManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslationManagerApplication.class, args);
    }

}
