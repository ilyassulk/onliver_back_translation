package ru.onliver.translation_manager.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.onliver.translation_manager.model.ControlRequest;
import ru.onliver.translation_manager.model.StreamerControlResponse;
import ru.onliver.translation_manager.model.StreamerTranslationResponse;
import ru.onliver.translation_manager.model.Translation;
import ru.onliver.translation_manager.model.TranslationRequest;
import ru.onliver.translation_manager.repository.TranslationRepository;

@Service
@AllArgsConstructor
public class TranslationService {

    final RestTemplate restTemplate;
    final TranslationRepository translationRepository;

    public void startTranslation(TranslationRequest translationRequest) {
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
            translationRepository.deleteByRoomName(controlRequest.getRoomName());
        }

        translationRepository.save(translation);
    }

    public void stopTranslation(String roomName) {
        this.controlTranslation(new ControlRequest(roomName, ControlRequest.CommandType.STOP, 0L));
    }
}
