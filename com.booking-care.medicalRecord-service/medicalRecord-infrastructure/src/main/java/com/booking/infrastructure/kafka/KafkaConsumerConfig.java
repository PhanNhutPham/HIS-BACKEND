package com.booking.infrastructure.kafka;

import com.booking.infrastructure.event.AppointmentConfirmed;
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
    public ConsumerFactory<String, AppointmentConfirmed> consumerFactory() {
        JsonDeserializer<AppointmentConfirmed> deserializer = new JsonDeserializer<>(AppointmentConfirmed.class);
        deserializer.addTrustedPackages("*"); // chấp nhận tất cả các package
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(true); // nếu key cũng là JSON thì dùng true, nếu chỉ là String thì bỏ
        // tùy chọn: cho phép JSON lỗi nhẹ

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9193");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "medicalRecord-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentConfirmed> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AppointmentConfirmed> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
