package ru.onliver.translation_gc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.onliver.translation_gc.enums.KafkaTranslationEventType;
import ru.onliver.translation_gc.model.TranslationEvent;
import ru.onliver.translation_gc.repository.TranslationRepository;
import ru.onliver.translation_gc.util.KafkaProducer;

/**
 * Сервис сборки мусора трансляций, проверяющий активность трансляций
 * и обрабатывающий их завершение в случае обнаружения неактивности.
 * Запускается по расписанию каждую минуту для проверки всех трансляций.
 */
@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class TranslationGCService {

    final RestTemplate restTemplate;
    final KafkaProducer kafkaProducer;
    final TranslationRepository translationRepository;

    @Scheduled(fixedDelay = 60000)
    void run(){
        translationRepository.findAll().forEach(translation -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<?> entity =
                        new HttpEntity<>(null, headers);

                ResponseEntity<String> response =
                        restTemplate.postForEntity(
                                "http://"+translation.getStreamerIP()+":8080/translation/"+ translation.getRoomName() +"/check",
                                entity,
                                String.class
                        );

                if (!response.getStatusCode().is2xxSuccessful()){
                    log.info("Translation ended from room {}", translation.getRoomName());
                    handleEndTranslation(translation.getRoomName());
                }
            }
            catch(Exception e){
                log.info("Exception while checking translation from room {}", translation.getRoomName(), e);
                handleEndTranslation(translation.getRoomName());
            }
        });
    }

    void handleEndTranslation(String roomName){
        kafkaProducer.send("translation-events", new TranslationEvent(KafkaTranslationEventType.TRANSLATION_ENDED_EMERGENCY, roomName));
    }
}
