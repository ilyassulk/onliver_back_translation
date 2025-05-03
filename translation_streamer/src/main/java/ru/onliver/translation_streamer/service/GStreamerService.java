package ru.onliver.translation_streamer.service;

import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.event.SeekFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.onliver.translation_streamer.enums.KafkaTranslationEventType;
import ru.onliver.translation_streamer.model.ControlRequest;
import ru.onliver.translation_streamer.model.IngressInfo;
import ru.onliver.translation_streamer.model.TranslationEvent;
import ru.onliver.translation_streamer.model.TranslationRequest;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Format;
import ru.onliver.translation_streamer.util.KafkaProducer;

/**
 * Сервис для работы с GStreamer.
 * Управляет медиа-стримами, обеспечивает трансляцию контента
 * и обработку команд управления воспроизведением.
 */
@Service
public class GStreamerService {

    public static final long NSECOND = 1_000_000_000L;

    private static final Logger logger = LoggerFactory.getLogger(GStreamerService.class);

    private final LiveKitService liveKitService;
    private final SyncMessageService syncMessageService;
    private final TranslationProducerService translationProducerService;

    public static class ActiveStream {
        public Pipeline pipeline;
        final String ingressId;

        ActiveStream(Pipeline pipeline, String ingressId) {
            this.pipeline = pipeline;
            this.ingressId = ingressId;
        }
    }
     final Map<String, ActiveStream> activeStreams = new ConcurrentHashMap<>();

    public GStreamerService(LiveKitService liveKitService,  SyncMessageService syncMessageService, TranslationProducerService translationProducerService) {
        this.liveKitService = liveKitService;
        this.syncMessageService = syncMessageService;
        this.translationProducerService = translationProducerService;
    }

    @PostConstruct
    public void initializeGStreamer() {
        try {
            Gst.init(Version.BASELINE, "TranslationStreamer");
            logger.info("GStreamer initialized: {}", Gst.getVersionString());
        } catch (Throwable t) {
            logger.error("Failed to initialize GStreamer", t);
            System.exit(1);
        }
    }

    @PreDestroy
    public void deinitializeGStreamer() {
        activeStreams.keySet().forEach(this::stopTranslationInternal);
        Gst.deinit();
        logger.info("GStreamer deinitialized.");
    }

    public void startTranslation(TranslationRequest request) {
        String roomName = request.getRoomName();
        String contentURL = request.getContentURL();

        String participantIdentity = "streamer-" + roomName;
        String participantName = "Video File Streamer";

        if (activeStreams.containsKey(roomName)) {
            logger.warn("Translation already active for room: {}", roomName);
            return;
        }

        // 1. Создаем LiveKit Ingress
        // НУ ОЧЕВИДНО ЧТО ЭТО НЕ ДОЛЖНО ТУТ БЫТЬ!
        // ЕСЛИ СОЗДАНИЕ ИНГРЕСА ВСЁ ЕЩЁ ЧТО_ТО ЗАБЫЛО В СЕРВИСЕ G-СТРИМЕРА - КАЮСЬ!
        IngressInfo ingressInfo;
        try {
            ingressInfo = liveKitService.createIngress(roomName, participantIdentity, participantName);
            if (ingressInfo == null || ingressInfo.getUrl() == null || ingressInfo.getUrl().isEmpty()) {
                logger.error("Failed to get valid Ingress URL for room: {}", roomName);
                return; // Не удалось создать Ingress
            }
        } catch (Exception e) {
            logger.error("Failed to create LiveKit Ingress for room {}: {}", roomName, e.getMessage(), e);
            return; // Не удалось создать Ingress
        }

        String ingressURL = ingressInfo.getUrl() +"/"+ ingressInfo.getStreamKey();
        String pipelineDescription = String.format(
                "souphttpsrc location=\"%s\" ! decodebin name=dec " +
                "dec. ! queue ! videoconvert ! x264enc tune=zerolatency bitrate=1000 ! " +
                "h264parse ! queue ! mux. " +
                "dec. ! queue ! audioconvert ! audioresample ! voaacenc bitrate=128000 ! " +
                "aacparse ! queue ! mux. " +
                "flvmux streamable=true name=mux ! rtmpsink location=\"%s\""
                ,
                contentURL,
                ingressURL
        );


        logger.info("Attempting to start GStreamer pipeline for room {}: {}", roomName, pipelineDescription);
        AtomicReference<Pipeline> pipelineRef = new AtomicReference<>();

        try {
            Pipeline pipeline = (Pipeline) Gst.parseLaunch(pipelineDescription);
            pipeline.setName(roomName); // Устанавливаем имя для логирования
            pipelineRef.set(pipeline); // Сохраняем для возможной очистки при ошибке

            // Сохраняем ingressId для последующего удаления
            final String currentIngressId = ingressInfo.getIngressId();

            pipeline.getBus().connect((Bus.EOS) source -> {
                logger.info("End-Of-Stream reached for room: {}", source.getName());
                translationProducerService.publishTranslationEndPlannedEvent(roomName);
                stopTranslationInternal(source.getName()); // Останавливаем и удаляем Ingress
            });

            pipeline.getBus().connect((Bus.ERROR) (source, code, message) -> {
                logger.error("GStreamer error for room {}: code={}, message={}", source.getName(), code, message);
                translationProducerService.publishTranslationEndEmergencyEvent(roomName);
                stopTranslationInternal(source.getName()); // Останавливаем и удаляем Ingress
            });

            // Запускаем пайплайн
            StateChangeReturn stateChange = pipeline.play();
            if (stateChange.equals(StateChangeReturn.FAILURE)) {
                 logger.error("Failed to set pipeline to PLAYING state for room: {}", roomName);
                 throw new RuntimeException("Failed to start pipeline");
            }

            // Сохраняем активный стрим
            activeStreams.put(roomName, new ActiveStream(pipeline, currentIngressId));
            logger.info("Translation pipeline started successfully for room: {} with Ingress ID: {}", roomName, currentIngressId);

        } catch (Exception e) {
            logger.error("Failed to parse or start GStreamer pipeline for room: {}", roomName, e);
            // Если пайплайн создан, но не запущен, очищаем его
            Pipeline createdPipeline = pipelineRef.get();
            if (createdPipeline != null) {
                createdPipeline.dispose();
            }
            // Удаляем Ingress, если он был создан, но пайплайн не стартанул
            if (ingressInfo != null && ingressInfo.getIngressId() != null) {
                 logger.warn("Cleaning up Ingress {} for room {} due to pipeline start failure", ingressInfo.getIngressId(), roomName);
                 liveKitService.deleteIngress(ingressInfo.getIngressId());
            }
        }
    }

    public void controlTranslation(ControlRequest command) {
        String roomName = command.getRoomName();
        ActiveStream activeStream = activeStreams.get(roomName);

        if (activeStream == null || activeStream.pipeline == null) {
            logger.warn("No active translation found for room: {} to apply command: {}", roomName, command.getCommand());
            return;
        }
        Pipeline pipeline = activeStream.pipeline;

        switch (command.getCommand()) {
            case PAUSE:
                logger.info("Pausing translation for room: {}", roomName);
                pipeline.pause();
                syncMessageService.sendSyncMessage(roomName, pipeline);
                break;
            case PLAY:
                logger.info("Resuming translation for room: {}", roomName);
                pipeline.play();
                syncMessageService.sendSyncMessage(roomName, pipeline);
                break;
            case SEEK:
                if (command.getSeekTime() != null) {
                    long seekTimeNs = command.getSeekTime() * NSECOND;
                    logger.info("Seeking translation for room: {} to {} ns", roomName, seekTimeNs);
                    boolean seeked = pipeline.seekSimple(
                            Format.TIME,                // формат времени (наносекунды)
                            EnumSet.of(SeekFlags.KEY_UNIT), // флаги (очистка)
                            seekTimeNs                // новая позиция в наносекундах
                    );

                    if (!seeked) {
                        logger.warn("Seek operation failed or was inaccurate for room: {}", roomName);
                    }
                    else {
                        syncMessageService.sendSyncMessage(roomName, pipeline);
                    }
                } else {
                    logger.warn("Seek time not provided for SEEK command in room: {}", roomName);
                }
                break;
            case STOP:
                logger.info("Stopping translation for room: {} via control command", roomName);
                syncMessageService.sendEndMessage(roomName);
                stopTranslationInternal(roomName);
                translationProducerService.publishTranslationEndManualEvent(roomName);
                break;
            default:
                logger.warn("Unknown control command: {}", command.getCommand());
        }
    }

    private void stopTranslationInternal(String roomName) {
        ActiveStream activeStream = activeStreams.remove(roomName);
        if (activeStream != null) {
            Pipeline pipeline = activeStream.pipeline;
            String ingressId = activeStream.ingressId;
            logger.info("Stopping pipeline and cleaning up resources for room: {}, Ingress ID: {}", roomName, ingressId);
            try {
                StateChangeReturn stateChange = pipeline.stop();
                logger.info("Pipeline stop command issued for room: {}. Result: {}", roomName, stateChange);
                pipeline.dispose();
                logger.info("Pipeline disposed for room: {}", roomName);
            } catch (Exception e) {
                logger.error("Error during stopping/disposing pipeline for room: {}", roomName, e);
            }

            // Удаляем LiveKit Ingress
            if (ingressId != null) {
                liveKitService.deleteIngress(ingressId);
            }
        } else {
             logger.debug("Stop command for room {} received, but no active stream found (already stopped?).", roomName);
        }
    }

    public  boolean checkRoomTranslation(String roomName){
        try {
            ActiveStream activeStream = activeStreams.get(roomName);
            if (activeStream == null) {
                return false;
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean isManagingStream(String roomName) {
        return activeStreams.containsKey(roomName);
    }
}
