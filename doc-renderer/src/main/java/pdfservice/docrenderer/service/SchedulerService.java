package pdfservice.docrenderer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docrenderer.repository.OutboxFinishedJobsRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulerService {
    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private OutboxFinishedJobsRepository repository;
    private KafkaTemplate<Object, Object> kafkaTemplate;

    private final Duration duration = Duration.ofSeconds(2);

    @Autowired
    public SchedulerService(OutboxFinishedJobsRepository repo, KafkaTemplate<Object, Object> kafkaTemplate){
        this.repository = repo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 1000)
    public void readEventsFromOutbox(){
        List<OutboxFinishedJobRecord> events = repository.getFiveEvents();
        List<CompletableFuture<SendResult<Object, Object>>> futures = new ArrayList<>();
        for (var event: events){
            CompletableFuture<SendResult<Object, Object>> future = kafkaTemplate.send("jobs.result", event);
            futures.add(future);
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            all.orTimeout(duration.toMillis(), TimeUnit.MILLISECONDS).join();
        } catch (Exception e){
            log.info("Some events failed to send to kafka");
        }
        List<OutboxFinishedJobRecord> finishedEvents = new ArrayList<>();
        List<OutboxFinishedJobRecord> errorEvents = new ArrayList<>();
        for (int i = 0 ;i < events.size(); i++){
            if (futures.get(i).isDone()){
                try {
                    futures.get(i).join();
                    finishedEvents.add(events.get(i));
                } catch (Exception e){
                    errorEvents.add(events.get(i));
                }
            }
        }
        if (!errorEvents.isEmpty()) {
            log.info("failed to send some events {}", errorEvents);
        }
        if (!finishedEvents.isEmpty()){
            log.info("Succeed to send events {}", finishedEvents);
        }
        repository.saveEvents(finishedEvents);
    }
}
