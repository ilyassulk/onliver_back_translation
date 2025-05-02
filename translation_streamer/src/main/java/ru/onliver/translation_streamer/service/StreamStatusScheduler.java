package ru.onliver.translation_streamer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import livekit.LivekitModels;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.Format;
import org.freedesktop.gstreamer.Pipeline;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

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
