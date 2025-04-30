package ru.onliver.translation_streamer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import livekit.LivekitModels;
import lombok.RequiredArgsConstructor;
import org.freedesktop.gstreamer.Format;
import org.freedesktop.gstreamer.Pipeline;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class StreamStatusScheduler {
    private final GStreamerService gStreamerService;
    private final LiveKitService liveKitService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Scheduled(fixedRate = 5000)
    public void pushStatuses() {
        gStreamerService.activeStreams.forEach((room, active) -> {
            long posNs = safeQuery(active.pipeline);
            //if (posNs < 0) return;
            long posMs = posNs / 1_000_000L;

            byte[] payload = null;
            try {
                payload = mapper.writeValueAsBytes(
                        Map.of("room", room,
                                "positionMs", posMs));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            liveKitService.roomClient.sendData(room, payload,     LivekitModels.DataPacket.Kind.RELIABLE,   // режим доставки
                    Collections.emptyList(),                  // destinationSids
                    Collections.emptyList(),                  // destinationIdentities
                    "stream-status"        );
        });
    }

    /** надёжный запрос позиции, возвращает -1 при ошибке */
    private long safeQuery(Pipeline p) {
        try { return p.queryPosition(Format.TIME); }      // GStreamer API
        catch (Exception e) { return -1; }
    }
}
