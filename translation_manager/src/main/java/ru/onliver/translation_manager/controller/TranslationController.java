package ru.onliver.translation_manager.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.onliver.translation_manager.model.ControlRequest;
import ru.onliver.translation_manager.model.TranslationRequest;
import ru.onliver.translation_manager.service.TranslationService;

/**
 * REST-контроллер для управления трансляциями (запуск и контроль)
 */
@RestController
@RequestMapping("/translation")
@AllArgsConstructor
public class TranslationController {

    final TranslationService translationService;

    @PostMapping("/start")
    public ResponseEntity<?> startTranslation(@RequestBody TranslationRequest request) {
        translationService.startTranslation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/control")
    public ResponseEntity<?> controlTranslation(@RequestBody ControlRequest command) {
        translationService.controlTranslation(command);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}