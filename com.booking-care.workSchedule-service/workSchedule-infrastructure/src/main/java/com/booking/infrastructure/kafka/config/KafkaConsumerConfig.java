package com.booking.infrastructure.kafka.config;

import com.booking.infrastructure.kafka.event.CreateWorkScheduleEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, CreateWorkScheduleEvent> workScheduleConsumerFactory() {
        JsonDeserializer<CreateWorkScheduleEvent> deserializer = new JsonDeserializer<>(CreateWorkScheduleEvent.class);
        deserializer.addTrustedPackages("com.booking.infrastructure.kafka.event"); // cụ thể, không dùng '*'
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(false); // KHÔNG deserialize key, nên để false

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9193"); // Kiểm tra lại port đúng chưa
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "workSchedule-service-group-v2");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.booking.infrastructure.kafka.event"); // Quan trọng

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreateWorkScheduleEvent> workScheduleKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CreateWorkScheduleEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(workScheduleConsumerFactory());
        return factory;
    }
}
