package pdfservice.docapi.common.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docapi.common.api.service.JobService;
import pdfservice.docapi.common.api.service.S3Service;
import pdfservice.docapi.common.api.service.WebHookService;

@Slf4j
@Service
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class FinishedJobListener {
    private JobService jobService;
    private S3Service s3Service;
    private WebHookService webHookService;

    @Autowired
    public FinishedJobListener(JobService  jobService, S3Service s3Service, WebHookService webHookService){
        this.jobService = jobService;
        this.s3Service = s3Service;
        this.webHookService = webHookService;
    }

    @KafkaListener(
            id = "id2",
            topics = "jobs.result",
            concurrency = "5",
            containerFactory = "concurrentListenerContainerFactory"
    )
    public void listen(OutboxFinishedJobRecord event, Acknowledgment ack){
        log.info("Received event {}", event);
        String webHookUrl = jobService.finishEventAndGetWebHookUrl(event);
        webHookService.uploadFileToUrl(webHookUrl, event.jobId(), s3Service.getSignedUrl(event.key()));
        log.info("Uploaded file {}", s3Service.getSignedUrl(event.key()));
        ack.acknowledge();
    }
}
