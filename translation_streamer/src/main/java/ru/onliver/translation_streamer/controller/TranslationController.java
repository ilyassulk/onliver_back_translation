package ru.onliver.translation_streamer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.onliver.translation_streamer.model.ControlCommand;
import ru.onliver.translation_streamer.model.TranslationRequest;
import ru.onliver.translation_streamer.service.GStreamerService;

@RestController
@RequestMapping("/api/translation")
public class TranslationController {

    private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);
    private final GStreamerService gStreamerService;

    public TranslationController(GStreamerService gStreamerService) {
        this.gStreamerService = gStreamerService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startTranslation(@RequestBody TranslationRequest request) {
        if (request == null || request.getRoomName() == null || request.getRoomName().isEmpty()
                || request.getMinioFilePath() == null || request.getMinioFilePath().isEmpty()) {
            logger.warn("Invalid start translation request: {}", request);
            return ResponseEntity.badRequest().body("Room name and MinIO file path are required.");
        }

        logger.info("Received request to start translation for room: {}", request.getRoomName());
        try {
            gStreamerService.startTranslation(request);
            return ResponseEntity.ok("Translation initiation request accepted for room: " + request.getRoomName());
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

    @PostMapping("/control")
    public ResponseEntity<String> controlTranslation(@RequestBody ControlCommand command) {
        if (command == null || command.getRoomName() == null || command.getRoomName().isEmpty() || command.getCommand() == null) {
            logger.warn("Invalid control command request: {}", command);
            return ResponseEntity.badRequest().body("Room name and command type are required.");
        }
        if (command.getCommand() == ControlCommand.CommandType.SEEK && command.getSeekTime() == null) {
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