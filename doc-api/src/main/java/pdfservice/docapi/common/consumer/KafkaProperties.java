package pdfservice.docapi.common.consumer;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docapi.common.api.dto.OutboxRecord;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProperties {
    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name("jobs.queue")
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newTopic2(){
        return TopicBuilder.name("jobs.result")
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean("concurrentListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboxFinishedJobRecord> kafkaListenerContainerFactory(ConsumerFactory<String, OutboxFinishedJobRecord> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OutboxFinishedJobRecord> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OutboxFinishedJobRecord> consumerFactory() {
        JacksonJsonDeserializer<OutboxFinishedJobRecord> deserializer = new JacksonJsonDeserializer<>(OutboxFinishedJobRecord.class);
        deserializer.addTrustedPackages("pdfservice.docapi.common.api.dto");
        return new DefaultKafkaConsumerFactory<>(consumerProps(), new StringDeserializer(), deserializer);
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }
}
