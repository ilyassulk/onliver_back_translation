package ru.onliver.translation_streamer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.onliver.translation_streamer.model.ControlRequest;
import ru.onliver.translation_streamer.model.TranslationRequest;
import ru.onliver.translation_streamer.model.TranslationResponse;
import ru.onliver.translation_streamer.service.GStreamerService;
import ru.onliver.translation_streamer.service.HardwareService;

/**
 * REST-контроллер для управления стримами перевода.
 * Предоставляет API для запуска, проверки и управления трансляциями.
 * Обрабатывает запросы для начала трансляции и контроля над ней.
 */
@RestController
public class TranslationController {

    private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);
    private final GStreamerService gStreamerService;
    public final HardwareService hardwareService;

    public TranslationController(GStreamerService gStreamerService, HardwareService hardwareService) {
        this.gStreamerService = gStreamerService;
        this.hardwareService = hardwareService;
    }

    @GetMapping("/translation/{roomName}/check")
    public ResponseEntity<?> checkTranslation(@PathVariable String roomName) {
        if(roomName == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            if(gStreamerService.checkRoomTranslation(roomName)){
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/translation/start")
    public ResponseEntity<?> startTranslation(@RequestBody TranslationRequest request) {
        if (request == null || request.getRoomName() == null || request.getRoomName().isEmpty()
                || request.getContentURL() == null || request.getContentURL().isEmpty()) {
            logger.warn("Invalid start translation request: {}", request);
            return ResponseEntity.badRequest().body("Room name and MinIO file path are required.");
        }

        logger.info("Received request to start translation for room: {}", request.getRoomName());
        try {
            gStreamerService.startTranslation(request);
            return ResponseEntity.ok(new TranslationResponse(request.getRoomName(), hardwareService.getContainerIp()));
        } catch (IllegalStateException e) {
            logger.error("Configuration or state error starting translation for room {}: {}", request.getRoomName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
             logger.error("Error creating LiveKit Ingress for room {}: {}", request.getRoomName(), e.getMessage(), e);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to prepare translation resources (LiveKit Ingress).");
        } catch (Exception e) {
            logger.error("Unexpected error starting translation for room {}: {}", request.getRoomName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start translation due to an unexpected error.");
        }
    }

    @PostMapping("/translation/control")
    public ResponseEntity<String> controlTranslation(@RequestBody ControlRequest command) {
        if (command == null || command.getRoomName() == null || command.getRoomName().isEmpty() || command.getCommand() == null) {
            logger.warn("Invalid control command request: {}", command);
            return ResponseEntity.badRequest().body("Room name and command type are required.");
        }
        if (command.getCommand() == ControlRequest.CommandType.SEEK && command.getSeekTime() == null) {
             logger.warn("Invalid SEEK command: seekTime is missing for room {}", command.getRoomName());
             return ResponseEntity.badRequest().body("Seek time is required for SEEK command.");
        }

        logger.info("Received control command {} for room: {}", command.getCommand(), command.getRoomName());
        try {
            if (!gStreamerService.isManagingStream(command.getRoomName())) {
                 logger.warn("Received control command for room {} which is not managed by this instance.", command.getRoomName());
                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No active translation found for room: " + command.getRoomName() + " managed by this instance.");
            }

            gStreamerService.controlTranslation(command);
            return ResponseEntity.ok("Command " + command.getCommand() + " executed for room: " + command.getRoomName());
        } catch (Exception e) {
            logger.error("Error executing control command {} for room {}: {}", command.getCommand(), command.getRoomName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to execute control command.");
        }
    }
} 