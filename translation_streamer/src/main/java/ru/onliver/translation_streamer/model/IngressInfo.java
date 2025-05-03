package ru.onliver.translation_streamer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Модель информации о LiveKit Ingress.
 * Содержит данные о созданном Ingress для трансляции.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngressInfo {
    private String url; // URL для отправки потока (например, rtmp://... или udp://...)
    private String streamKey; // Ключ потока (если используется, например, для RTMP)
    private String ingressId; // ID созданного Ingress
} 