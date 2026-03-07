package pdfservice.docrenderer.service;

import freemarker.template.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pdfservice.docapi.common.api.dto.OutboxEvent;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docapi.common.api.dto.OutboxPayload;
import pdfservice.docrenderer.repository.OutboxFinishedJobsRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocGeneratorServiceTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private JobService jobService;

    @Mock
    private Configuration configuration;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private S3Service s3Service;

    @Mock
    private OutboxFinishedJobsRepository repository;

    @Test
    void handle_savesFinishedEvent() {
        DocGeneratorService real = new DocGeneratorService(
                templateService,
                jobService,
                configuration,
                objectMapper,
                s3Service,
                repository
        );

        DocGeneratorService service = spy(real);

        UUID jobId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID templateVersionId = UUID.randomUUID();
        String requestId = "req-1";
        String key = "file-" + jobId + ".pdf";

        OutboxPayload payload = new OutboxPayload(jobId, tenantId, requestId, templateVersionId);
        OutboxEvent event = OutboxEvent.builder()
                .aggregatedType("job")
                .aggregateId(jobId)
                .eventType("job.queued")
                .payload(payload)
                .build();

        doReturn(key).when(service).generateByTemplateId(eq(templateVersionId), eq(jobId));

        service.handle(event);

        verify(repository).saveEvent(new OutboxFinishedJobRecord(jobId, key));
    }
}
