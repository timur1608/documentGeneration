package pdfservice.docapi.common.api.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import pdfservice.docapi.common.api.dto.OutboxRecord;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private OutboxRepository outboxRepository;

    @Test
    void publishJob_insertsEvent() {
        UUID jobId = UUID.randomUUID();
        String payload = "{\"a\":1}";

        outboxRepository.publishJob("job", jobId, "job.queued", payload);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(sqlCaptor.capture(), eq("job"), eq(jobId), eq("job.queued"), eq(payload));
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO outbox"));
    }

    @Test
    void getFiveEvents_returnsFromJdbcTemplate() {
        List<OutboxRecord> events = List.of(
                new OutboxRecord("job", UUID.randomUUID(), "job.queued", "{\"a\":1}")
        );

        when(jdbcTemplate.query(
                anyString(),
                ArgumentMatchers.<org.springframework.jdbc.core.RowMapper<OutboxRecord>>any()
        )).thenReturn(events);

        List<OutboxRecord> result = outboxRepository.getFiveEvents();

        assertEquals(events, result);
    }

    @Test
    void saveEvents_emptyList_skipsBatchUpdate() {
        outboxRepository.saveEvents(List.of());

        verify(jdbcTemplate, never()).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
    }

    @Test
    void saveEvents_updatesAllProvidedIds() throws Exception {
        OutboxRecord e1 = new OutboxRecord("job", UUID.randomUUID(), "job.queued", "{}");
        OutboxRecord e2 = new OutboxRecord("job", UUID.randomUUID(), "job.queued", "{}");

        outboxRepository.saveEvents(List.of(e1, e2));

        ArgumentCaptor<BatchPreparedStatementSetter> setterCaptor =
                ArgumentCaptor.forClass(BatchPreparedStatementSetter.class);
        verify(jdbcTemplate).batchUpdate(anyString(), setterCaptor.capture());

        BatchPreparedStatementSetter setter = setterCaptor.getValue();
        PreparedStatement ps = mock(PreparedStatement.class);

        setter.setValues(ps, 0);
        verify(ps).setObject(1, e1.aggregateId());

        setter.setValues(ps, 1);
        verify(ps).setObject(1, e2.aggregateId());

        assertEquals(2, setter.getBatchSize());
    }
}
