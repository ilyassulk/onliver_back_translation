package ru.onliver.translation_manager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.config.TopicBuilder;
import ru.onliver.translation_manager.model.RoomEvent;
import ru.onliver.translation_manager.model.TranslationEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /* ---------- общая «болванка» пропсов для всех consumer-ов ---------- */
    private Map<String, Object> commonConsumerProps(Class<?> targetType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,            groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.onliver.translation_manager.model");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, targetType.getName());
        return props;
    }

    /* ---------- RoomEvent ---------- */
    @Bean
    public ConsumerFactory<String, RoomEvent> roomEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                commonConsumerProps(RoomEvent.class),
                new StringDeserializer(),
                new JsonDeserializer<>(RoomEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoomEvent>
    roomKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoomEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(roomEventConsumerFactory());
        factory.setMissingTopicsFatal(false);
        return factory;
    }

    /* ---------- TranslationEvent ---------- */
    @Bean
    public ConsumerFactory<String, TranslationEvent> translationEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                commonConsumerProps(TranslationEvent.class),
                new StringDeserializer(),
                new JsonDeserializer<>(TranslationEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TranslationEvent>
    translationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TranslationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(translationEventConsumerFactory());
        factory.setMissingTopicsFatal(false);
        return factory;
    }

    /* ---------- сами топики (создаются при старте, если ещё нет) ---------- */
    @Bean
    public NewTopic roomEventsTopic() {
        return TopicBuilder.name("room-events").build();
    }

    @Bean
    public NewTopic translationEventsTopic() {
        return TopicBuilder.name("translation-events").build();
    }
}
