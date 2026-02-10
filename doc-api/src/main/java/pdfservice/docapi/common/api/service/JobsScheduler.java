package pdfservice.docapi.common.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pdfservice.docapi.common.api.dto.OutboxRecord;
import pdfservice.docapi.common.api.repository.OutboxRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class JobsScheduler {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    @Autowired
    public JobsScheduler(KafkaTemplate<Object, Object> template, OutboxRepository outboxRepository){
        this.kafkaTemplate = template;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedRate = 3000)
    public void send() throws ExecutionException, InterruptedException {
        List<OutboxRecord> events = outboxRepository.getFiveEvents();
        for (var event : events){
            kafkaTemplate.send("jobs.queue", event).get();
        }
        outboxRepository.saveEvents(events);
    }
}
