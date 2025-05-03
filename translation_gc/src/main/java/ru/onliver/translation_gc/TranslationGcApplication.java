package ru.onliver.translation_gc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Spring Boot, запускающий микросервис сборщика мусора трансляций.
 * Отвечает за инициализацию контекста Spring и запуск сервиса.
 */
@SpringBootApplication
public class TranslationGcApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranslationGcApplication.class, args);
    }

}
