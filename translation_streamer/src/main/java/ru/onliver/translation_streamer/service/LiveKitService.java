package ru.onliver.translation_streamer.service;

import io.livekit.server.IngressServiceClient;
import livekit.LivekitIngress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import ru.onliver.translation_streamer.model.IngressInfo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class LiveKitService {

    private static final Logger logger = LoggerFactory.getLogger(LiveKitService.class);

    @Value("${livekit.host}")
    private String livekitHost;

    @Value("${livekit.apiKey}")
    private String apiKey;

    @Value("${livekit.secret}")
    private String apiSecret;

    private IngressServiceClient ingressClient;

    @PostConstruct
    public void initialize() {
        try {
            this.ingressClient =  IngressServiceClient.create(livekitHost, apiKey, apiSecret);
            logger.info("LiveKit IngressServiceClient initialized for host: {}", livekitHost);
        } catch (Exception e) {
            logger.error("Failed to initialize LiveKit IngressServiceClient", e);
        }
    }

    public ru.onliver.translation_streamer.model.IngressInfo createIngress(String roomName, String participantIdentity, String participantName) {
        if (ingressClient == null) {
            logger.error("IngressServiceClient is not initialized.");
            throw new IllegalStateException("LiveKit service not available.");
        }

        logger.info("Creating LiveKit Ingress for room: {}, participant: {}", roomName, participantIdentity);

        LivekitIngress.CreateIngressRequest request = LivekitIngress.CreateIngressRequest.newBuilder()
                .setInputType(LivekitIngress.IngressInput.RTMP_INPUT)
                .setName("ingress-" + roomName + "-" + participantIdentity)
                .setRoomName(roomName)
                .setParticipantIdentity(participantIdentity)
                .setParticipantName(participantName)
                .build();

        try {
            Call<LivekitIngress.IngressInfo> info_с =  ingressClient.createIngress(
                    request.getRoomName(),
                    request.getRoomName(),
                    request.getParticipantIdentity(),
                    request.getParticipantName(),
                    request.getInputType(),
                    null,
                    null,
                    null,
                    null
            );
            Response<LivekitIngress.IngressInfo> info_r = info_с.execute();
            LivekitIngress.IngressInfo info = info_r.body();
            logger.info("Successfully created Ingress ID: {}, URL: {}, StreamKey: {}",
                    info.getIngressId(), info.getUrl(), info.getStreamKey());

            return new ru.onliver.translation_streamer.model.IngressInfo(info.getUrl(), info.getStreamKey(), info.getIngressId());

        } catch (Exception e) {
            logger.error("Failed to create LiveKit Ingress for room: {}, participant: {}",
                    roomName, participantIdentity, e);
            throw new RuntimeException("Failed to create LiveKit Ingress", e);
        }
    }

    public void deleteIngress(String ingressId) {
        if (ingressClient == null) {
            logger.error("IngressServiceClient is not initialized. Cannot delete ingress ID: {}", ingressId);
            return;
        }

        if (ingressId == null || ingressId.isEmpty()) {
            logger.warn("Cannot delete ingress with null or empty ID.");
            return;
        }

        logger.info("Deleting LiveKit Ingress with ID: {}", ingressId);
        LivekitIngress.DeleteIngressRequest request = LivekitIngress.DeleteIngressRequest.newBuilder()
                .setIngressId(ingressId)
                .build();
        try {
            ingressClient.deleteIngress(request.getIngressId());
            logger.info("Successfully deleted Ingress ID: {}", ingressId);
        } catch (Exception e) {
            logger.error("Failed to delete LiveKit Ingress ID: {}", ingressId, e);
        }
    }

}
