package pdfservice.docrenderer.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import pdfservice.docapi.common.api.dto.OutboxRecord;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConsumerProperties {
    @Bean("concurrentListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboxRecord> kafkaListenerContainerFactory(ConsumerFactory<String, OutboxRecord> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OutboxRecord> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OutboxRecord> consumerFactory() {
        try (JacksonJsonDeserializer<OutboxRecord> deserializer = new JacksonJsonDeserializer<>(OutboxRecord.class)) {
            deserializer.addTrustedPackages("pdfservice.docapi.common.api.dto.OutboxRecord");
            return new DefaultKafkaConsumerFactory<>(consumerProps(), new StringDeserializer(), deserializer);
        }
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        return props;
    }
}
