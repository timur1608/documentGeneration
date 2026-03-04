package pdfservice.docrenderer.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import pdfservice.docapi.common.api.dto.OutboxEvent;
import pdfservice.docapi.common.api.dto.OutboxRecord;
import pdfservice.docapi.common.api.mapper.OutboxMapper;
import pdfservice.docrenderer.service.DocGeneratorService;

@Configuration
public class JobListener {
    private final DocGeneratorService service;
    private static final Logger logger = LoggerFactory.getLogger(JobListener.class);
    public JobListener(DocGeneratorService service){
        this.service = service;
    }
    @KafkaListener(
            id = "id",
            topics = "jobs.queue",
            concurrency = "5",
            containerFactory = "concurrentListenerContainerFactory"
    )
    public void listen(OutboxRecord event, Acknowledgment ack) {
        logger.info("Received event for job listener {}", event.toString());
        OutboxEvent outboxEvent = OutboxMapper.mapToOutboxEvent(event);
        service.handle(outboxEvent);
        ack.acknowledge();
    }
}
