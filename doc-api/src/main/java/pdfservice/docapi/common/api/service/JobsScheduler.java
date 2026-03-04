package pdfservice.docapi.common.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pdfservice.docapi.common.api.dto.OutboxRecord;
import pdfservice.docapi.common.api.repository.OutboxRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JobsScheduler {

    private final Duration sendWaitTimeout = Duration.ofSeconds(2);


    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    @Autowired
    public JobsScheduler(KafkaTemplate<Object, Object> template, OutboxRepository outboxRepository){
        this.kafkaTemplate = template;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedRate = 1000)
    public void send() {
        List<OutboxRecord> events = outboxRepository.getFiveEvents();
        List<CompletableFuture<SendResult<Object, Object>>> futures = new ArrayList<>();
        List<OutboxRecord> successEvents = new ArrayList<>();
        List<OutboxRecord> failedEvents = new ArrayList<>();
        for (var event : events){
            CompletableFuture<SendResult<Object, Object>> result = kafkaTemplate.send("jobs.queue", event);
            futures.add(result);
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            all.orTimeout(sendWaitTimeout.toMillis(), TimeUnit.MILLISECONDS).join();
        } catch (Exception e) {
            log.info("Not all events are sended properly");
        }
        for (int i = 0; i < events.size(); i++){
            if (!futures.get(i).isDone())
                continue;
            else {
                try {
                    futures.get(i).join();
                    successEvents.add(events.get(i));
                } catch (Exception e){
                    failedEvents.add(events.get(i));
                }
            }
        }

        outboxRepository.saveEvents(successEvents);
    }
}
