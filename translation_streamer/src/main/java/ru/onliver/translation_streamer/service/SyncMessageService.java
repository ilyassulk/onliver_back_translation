package ru.onliver.translation_streamer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import livekit.LivekitModels;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.Format;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.State;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncMessageService {
    private final LiveKitService liveKitService;
    private final ObjectMapper mapper = new ObjectMapper();

    public void sendSyncMessage(String roomName, Pipeline pipeline) {
        try {
            log.info("sync message");
            long posNs = queryPosition(pipeline);
            long durNs = queryDuration(pipeline);
            State state = queryState(pipeline);
            long posMs = posNs / 1_000_000L;
            long durMs = durNs / 1_000_000L;

            byte[] payload = null;
            try {
                payload = mapper.writeValueAsBytes(
                        Map.of("room", roomName,
                                "positionMs", posMs,
                                "durationMs", durMs,
                                "state", state.intValue()
                        ));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            liveKitService.roomClient.sendData(roomName, payload, LivekitModels.DataPacket.Kind.RELIABLE,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    "stream-status").execute();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long queryPosition(Pipeline p) {
        try {
            return p.queryPosition(Format.TIME);
        }      // GStreamer API
        catch (Exception e) { return -1; }
    }

    private long queryDuration(Pipeline p) {
        try {
            return p.queryDuration(Format.TIME);
        }      // GStreamer API
        catch (Exception e) { return -1; }
    }

    private State queryState(Pipeline p) {
        try {
            return p.getState();
        }      // GStreamer API
        catch (Exception e) { return State.NULL; }
    }
}
