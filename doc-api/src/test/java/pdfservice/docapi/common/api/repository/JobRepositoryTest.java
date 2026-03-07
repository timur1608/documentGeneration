package pdfservice.docapi.common.api.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JobRepository jobRepository;

    @Test
    void finishJob_returnsWebhookUrl() {
        UUID jobId = UUID.randomUUID();
        String url = "https://example.com/hook";

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(jobId))).thenReturn(url);

        String result = jobRepository.finishJob(jobId);

        assertEquals(url, result);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(sqlCaptor.capture(), eq(String.class), eq(jobId));
        assertTrue(sqlCaptor.getValue().contains("UPDATE jobs"));
    }

    @Test
    void createJobOrFindExisting_returnsId() {
        UUID id = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID templateVersionId = UUID.randomUUID();
        String requestId = "req-1";
        String payload = "{\"a\":1}";
        String webhookUrl = "https://example.com/hook";

        UUID expected = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(UUID.class),
                eq(id),
                eq(tenantId),
                eq(templateVersionId),
                eq(requestId),
                eq(payload),
                eq(webhookUrl)
        )).thenReturn(expected);

        UUID result = jobRepository.createJobOrFindExisting(
                id,
                tenantId,
                templateVersionId,
                requestId,
                payload,
                webhookUrl
        );

        assertEquals(expected, result);
    }

    @Test
    void findJobPayload_returnsPayload() {
        UUID jobId = UUID.randomUUID();
        String payload = "{\"a\":1}";

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(jobId))).thenReturn(payload);

        String result = jobRepository.findJobPayload(jobId);

        assertEquals(payload, result);
    }
}
