package ru.onliver.translation_manager.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.onliver.translation_manager.enums.KafkaTranslationEventType;
import ru.onliver.translation_manager.model.ControlRequest;
import ru.onliver.translation_manager.model.StreamerTranslationResponse;
import ru.onliver.translation_manager.model.Translation;
import ru.onliver.translation_manager.model.TranslationEvent;
import ru.onliver.translation_manager.model.TranslationRequest;
import ru.onliver.translation_manager.repository.TranslationRepository;
import ru.onliver.translation_manager.util.KafkaProducer;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TranslationService {

    final RestTemplate restTemplate;
    final TranslationRepository translationRepository;
    final KafkaProducer kafkaProducer;

    public void startTranslation(TranslationRequest translationRequest) {
        if(translationRepository.findOptionalByRoomName(translationRequest.getRoomName()).isPresent()) {
            throw new RuntimeException("Translation for room name already exists");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TranslationRequest> entity =
                new HttpEntity<>(translationRequest, headers);

        ResponseEntity<StreamerTranslationResponse> response =
                restTemplate.postForEntity(
                        "http://translation-streamer:8080/start",
                        entity,
                        StreamerTranslationResponse.class
                );

        if (!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Failed to start translation");
        }

        translationRepository.save(new Translation(
                response.getBody().getRoomName(),
                "none",
                1,
                response.getBody().getStreamerIP()
        ));
    }

    public void controlTranslation(ControlRequest controlRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ControlRequest> entity =
                new HttpEntity<>(controlRequest, headers);

        Translation translation = translationRepository.findByRoomName(controlRequest.getRoomName());

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://"+translation.getStreamerIP()+":8080/control",
                        entity,
                        String.class
                );

        if (!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Failed to start translation");
        }



        if(controlRequest.getCommand() == ControlRequest.CommandType.PAUSE)
            translation.setStatus(2);
        if(controlRequest.getCommand() == ControlRequest.CommandType.PLAY)
            translation.setStatus(1);

        if(controlRequest.getCommand() == ControlRequest.CommandType.STOP){
            cleanTranslation(controlRequest.getRoomName());
        }

        translationRepository.save(translation);
    }

    public void stopTranslation(String roomName) {
        this.controlTranslation(new ControlRequest(roomName, ControlRequest.CommandType.STOP, 0L));
    }

    public void abortTranslation(String roomName){
        stopTranslation(roomName);
        cleanTranslation(roomName);
    }

    public void cleanTranslation(String roomName){
        translationRepository.deleteByRoomName(roomName);
    }

}
