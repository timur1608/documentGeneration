package pdfservice.docrenderer.consumer;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import pdfservice.docapi.common.api.dto.OutboxEvent;
import pdfservice.docapi.common.api.dto.OutboxRecord;
import pdfservice.docapi.common.api.mapper.OutboxMapper;

@Configuration
public class JobListener {
    @KafkaListener(
            id = "id",
            topics = "jobs.queue",
            concurrency = "5",
            containerFactory = "concurrentListenerContainerFactory"
    )
    public void listen(OutboxRecord event) {
        OutboxEvent outboxEvent = OutboxMapper.mapToOutboxEvent(event);
        System.out.println(outboxEvent);
    }
}
