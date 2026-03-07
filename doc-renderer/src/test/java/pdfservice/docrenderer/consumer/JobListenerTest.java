package pdfservice.docrenderer.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import pdfservice.docapi.common.api.dto.OutboxEvent;
import pdfservice.docapi.common.api.dto.OutboxPayload;
import pdfservice.docapi.common.api.dto.OutboxRecord;
import pdfservice.docrenderer.service.DocGeneratorService;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobListenerTest {

    @Mock
    private DocGeneratorService docGeneratorService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private JobListener listener;

    @Test
    void listen_mapsEventAndAcks() throws Exception {
        UUID jobId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID templateVersionId = UUID.randomUUID();
        String requestId = "req-1";

        OutboxPayload payload = new OutboxPayload(jobId, tenantId, requestId, templateVersionId);
        String payloadJson = new ObjectMapper().writeValueAsString(payload);

        OutboxRecord record = new OutboxRecord("job", jobId, "job.queued", payloadJson);

        listener.listen(record, acknowledgment);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(docGeneratorService).handle(captor.capture());

        OutboxEvent mapped = captor.getValue();
        assertEquals("job", mapped.aggregatedType());
        assertEquals(jobId, mapped.aggregateId());
        assertEquals("job.queued", mapped.eventType());
        assertEquals(jobId, mapped.payload().jobId());
        assertEquals(tenantId, mapped.payload().tenantId());
        assertEquals(requestId, mapped.payload().requestId());
        assertEquals(templateVersionId, mapped.payload().templateVersionId());

        verify(acknowledgment).acknowledge();
    }
}
