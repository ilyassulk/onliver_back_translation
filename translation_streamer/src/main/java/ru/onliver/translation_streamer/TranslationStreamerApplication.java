package ru.onliver.translation_streamer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения, запускающий Spring Boot контейнер.
 * Отвечает за инициализацию и запуск всего приложения.
 */
@SpringBootApplication
public class TranslationStreamerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TranslationStreamerApplication.class, args);
	}

}
