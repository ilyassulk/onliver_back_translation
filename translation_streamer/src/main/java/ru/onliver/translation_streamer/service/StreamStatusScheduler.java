package ru.onliver.translation_streamer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


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
        gStreamerService.activeStreams.forEach((room, active) -> {
            try {
                syncMessageService.sendSyncMessage(room, active.pipeline);
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }




}
