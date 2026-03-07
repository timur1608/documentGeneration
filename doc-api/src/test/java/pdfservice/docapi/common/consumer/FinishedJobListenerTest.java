package pdfservice.docapi.common.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docapi.common.api.service.JobService;
import pdfservice.docapi.common.api.service.S3Service;
import pdfservice.docapi.common.api.service.WebHookService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinishedJobListenerTest {

    @Mock
    private JobService jobService;

    @Mock
    private S3Service s3Service;

    @Mock
    private WebHookService webHookService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private FinishedJobListener listener;

    @Test
    void listen_callsServicesAndAcks() {
        UUID jobId = UUID.randomUUID();
        String key = "file-key";
        OutboxFinishedJobRecord event = new OutboxFinishedJobRecord(jobId, key);

        String webHookUrl = "https://example.com/hook";
        String signedUrl = "https://example.com/signed";

        when(jobService.finishEventAndGetWebHookUrl(event)).thenReturn(webHookUrl);
        when(s3Service.getSignedUrl(key)).thenReturn(signedUrl);

        listener.listen(event, acknowledgment);

        verify(jobService).finishEventAndGetWebHookUrl(event);
        verify(webHookService).uploadFileToUrl(eq(webHookUrl), eq(jobId), eq(signedUrl));
        verify(s3Service, atLeastOnce()).getSignedUrl(key);
        verify(acknowledgment).acknowledge();
    }
}
