package pdfservice.docapi.common.api.mapper;

import org.junit.jupiter.api.Test;
import pdfservice.docapi.common.api.dto.OutboxEvent;
import pdfservice.docapi.common.api.dto.OutboxPayload;
import pdfservice.docapi.common.api.dto.OutboxRecord;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutboxMapperTest {

    @Test
    void mapToOutboxEvent_mapsAllFields() throws Exception {
        UUID jobId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID templateVersionId = UUID.randomUUID();
        String requestId = "req-1";

        OutboxPayload payload = new OutboxPayload(jobId, tenantId, requestId, templateVersionId);
        String payloadJson = new ObjectMapper().writeValueAsString(payload);

        OutboxRecord record = new OutboxRecord("job", jobId, "job.queued", payloadJson);

        OutboxEvent event = OutboxMapper.mapToOutboxEvent(record);

        assertEquals("job", event.aggregatedType());
        assertEquals(jobId, event.aggregateId());
        assertEquals("job.queued", event.eventType());
        assertEquals(jobId, event.payload().jobId());
        assertEquals(tenantId, event.payload().tenantId());
        assertEquals(requestId, event.payload().requestId());
        assertEquals(templateVersionId, event.payload().templateVersionId());
    }
}
