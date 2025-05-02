package ru.onliver.translation_manager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
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



    @Bean
    public ConsumerFactory<String, RoomEvent> roomEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "${spring.kafka.bootstrap-servers}");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "${spring.kafka.consumer.group-id}");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // где Jackson знает, что десериализовать в RoomEvent
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.onliver.translation_manager.model.RoomEvent");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.onliver.translation_manager.model");
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(RoomEvent.class, false)
        ); // конструктор JsonDeserializer(targetClass)
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoomEvent> roomKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoomEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(roomEventConsumerFactory());
        return factory;
    }

    // ----- 2) Factory для TranslationEvent -----
    @Bean
    public ConsumerFactory<String, TranslationEvent> translationEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "${spring.kafka.bootstrap-servers}");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "${spring.kafka.consumer.group-id}");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.onliver.translation_manager.model.TranslationEvent");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.onliver.translation_manager.model");
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(TranslationEvent.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TranslationEvent> translationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TranslationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(translationEventConsumerFactory());
        return factory;
    }

    @Bean
    public NewTopic roomEventsTopic() {
        return TopicBuilder.name("room-events").build();
    }

    @Bean
    public NewTopic translationEventsTopic() {
        return TopicBuilder.name("translation-events").build();
    }

} 