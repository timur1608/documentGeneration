package pdfservice.docapi.common.consumer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProperties {
    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name("jobs.queue")
                .partitions(5)
                .replicas(1)
                .build();
    }
}
