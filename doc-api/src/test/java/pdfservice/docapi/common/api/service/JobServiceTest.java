package pdfservice.docapi.common.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pdfservice.docapi.common.api.dto.CreateJobRequestDto;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docapi.common.api.repository.JobRepository;
import pdfservice.docapi.common.api.repository.OutboxRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private OutboxRepository outboxRepository;

    private ObjectMapper objectMapper;

    private JobService jobService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jobService = new JobService(jobRepository, outboxRepository, objectMapper);
    }

    @Test
    void createJob_publishesOutboxAndReturnsId() throws Exception {
        UUID tenantId = UUID.randomUUID();
        UUID templateVersionId = UUID.randomUUID();
        String requestId = "request-1";
        String webhookUrl = "https://example.com/hook";

        JsonNode requestPayload = objectMapper.readTree("{\"foo\":\"bar\"}");
        CreateJobRequestDto request = new CreateJobRequestDto(
                tenantId,
                templateVersionId,
                requestId,
                requestPayload,
                webhookUrl
        );

        UUID expectedJobId = UUID.randomUUID();
        when(jobRepository.createJobOrFindExisting(
                any(UUID.class),
                eq(tenantId),
                eq(templateVersionId),
                eq(requestId),
                eq(requestPayload.toString()),
                eq(webhookUrl)
        )).thenReturn(expectedJobId);

        UUID result = jobService.createJob(request);

        assertEquals(expectedJobId, result);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(outboxRepository).publishJob(eq("job"), eq(expectedJobId), eq("job.queued"), payloadCaptor.capture());

        JsonNode outboxPayload = objectMapper.readTree(payloadCaptor.getValue());
        assertEquals(expectedJobId.toString(), outboxPayload.get("jobId").asString());
        assertEquals(tenantId.toString(), outboxPayload.get("tenantId").asString());
        assertEquals(templateVersionId.toString(), outboxPayload.get("templateVersionId").asString());
        assertEquals(requestId, outboxPayload.get("requestId").asString());
    }

    @Test
    void finishEventAndGetWebHookUrl_returnsRepositoryValue() {
        UUID jobId = UUID.randomUUID();
        OutboxFinishedJobRecord event = new OutboxFinishedJobRecord(jobId, "key");
        String expectedUrl = "https://example.com/webhook";

        when(jobRepository.finishJob(jobId)).thenReturn(expectedUrl);

        String result = jobService.finishEventAndGetWebHookUrl(event);

        assertEquals(expectedUrl, result);
        verify(jobRepository).finishJob(jobId);
    }

    @Test
    void getJobPayload_returnsParsedJson() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findJobPayload(jobId)).thenReturn("{\"a\":1}");

        JsonNode result = jobService.getJobPayload(jobId);

        assertEquals(1, result.get("a").asInt());
    }
}
