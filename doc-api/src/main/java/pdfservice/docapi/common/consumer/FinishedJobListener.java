package pdfservice.docapi.common.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docapi.common.api.service.JobService;
import pdfservice.docapi.common.api.service.S3Service;

@Slf4j
@Service
public class FinishedJobListener {
    private JobService jobService;
    private S3Service s3Service;

    @Autowired
    public FinishedJobListener(JobService  jobService, S3Service s3Service){
        this.jobService = jobService;
        this.s3Service = s3Service;
    }

    @KafkaListener(
            id = "id2",
            topics = "jobs.result",
            concurrency = "5",
            containerFactory = "concurrentListenerContainerFactory"
    )
    public void listen(OutboxFinishedJobRecord event, Acknowledgment ack){
        log.info("Received event {}", event);
        jobService.finishEvent(event);
        log.info("Uploaded file {}", s3Service.getSignedUrl(event.key()));
        ack.acknowledge();
    }
}
