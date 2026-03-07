package pdfservice.docrenderer.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;

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
class OutboxFinishedJobsRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private OutboxFinishedJobsRepository repository;

    @Test
    void saveEvent_insertsRecord() {
        OutboxFinishedJobRecord event = new OutboxFinishedJobRecord(UUID.randomUUID(), "key");

        repository.saveEvent(event);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(sqlCaptor.capture(), eq(event.jobId()), eq(event.key()));
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO outbox_finished_jobs"));
    }

    @Test
    void getFiveEvents_returnsFromJdbcTemplate() {
        List<OutboxFinishedJobRecord> events = List.of(
                new OutboxFinishedJobRecord(UUID.randomUUID(), "key")
        );

        when(jdbcTemplate.query(
                anyString(),
                ArgumentMatchers.<org.springframework.jdbc.core.RowMapper<OutboxFinishedJobRecord>>any()
        )).thenReturn(events);

        List<OutboxFinishedJobRecord> result = repository.getFiveEvents();

        assertEquals(events, result);
    }

    @Test
    void saveEvents_emptyList_skipsBatchUpdate() {
        repository.saveEvents(List.of());

        verify(jdbcTemplate, never()).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
    }

    @Test
    void saveEvents_updatesAllProvidedIds() throws Exception {
        OutboxFinishedJobRecord e1 = new OutboxFinishedJobRecord(UUID.randomUUID(), "key1");
        OutboxFinishedJobRecord e2 = new OutboxFinishedJobRecord(UUID.randomUUID(), "key2");

        repository.saveEvents(List.of(e1, e2));

        ArgumentCaptor<BatchPreparedStatementSetter> setterCaptor =
                ArgumentCaptor.forClass(BatchPreparedStatementSetter.class);
        verify(jdbcTemplate).batchUpdate(anyString(), setterCaptor.capture());

        BatchPreparedStatementSetter setter = setterCaptor.getValue();
        PreparedStatement ps = mock(PreparedStatement.class);

        setter.setValues(ps, 0);
        verify(ps).setObject(1, e1.jobId());

        setter.setValues(ps, 1);
        verify(ps).setObject(1, e2.jobId());

        assertEquals(2, setter.getBatchSize());
    }
}
