package ru.onliver.translation_streamer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * Планировщик проверки статуса стримов.
 * Периодически проверяет состояние активных трансляций и обновляет их статус.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class StreamStatusScheduler {
    private final GStreamerService gStreamerService;
    final SyncMessageService syncMessageService;


    @Scheduled(fixedRate = 5000)
    public void pushStatuses() {
        // Создаем snapshot активных стримов для избежания ConcurrentModificationException
        Map<String, GStreamerService.ActiveStream> currentStreams = new HashMap<>(gStreamerService.activeStreams);
        
        currentStreams.forEach((room, active) -> {
            try {
                // Проверяем, что pipeline еще валиден
                if (active != null && active.pipeline != null && !active.isStopping) {
                    syncMessageService.sendSyncMessage(room, active.pipeline);
                }
            } catch (Exception e) {
                log.error("Error sending sync message for room {}: {}", room, e.getMessage());
                // Если pipeline поврежден или недоступен, можно рассмотреть его удаление
                if (e.getMessage() != null && e.getMessage().contains("disposed")) {
                    log.warn("Pipeline for room {} appears to be disposed, consider cleanup", room);
                }
            }
        });
    }




}
