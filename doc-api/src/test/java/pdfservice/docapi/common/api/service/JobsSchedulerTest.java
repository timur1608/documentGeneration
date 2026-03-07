package pdfservice.docapi.common.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import pdfservice.docapi.common.api.dto.OutboxRecord;
import pdfservice.docapi.common.api.repository.OutboxRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobsSchedulerTest {

    @Mock
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private JobsScheduler jobsScheduler;

    @Captor
    private ArgumentCaptor<List<OutboxRecord>> outboxRecordsCaptor;

    @Test
    void send_noEvents_savesEmptyList() {
        when(outboxRepository.getFiveEvents()).thenReturn(List.of());

        jobsScheduler.send();

        verify(kafkaTemplate, never()).send(anyString(), any());

        verify(outboxRepository).saveEvents(outboxRecordsCaptor.capture());
        assertTrue(outboxRecordsCaptor.getValue().isEmpty());
    }

    @Test
    void send_mixedResults_savesOnlySuccessful() {
        OutboxRecord event1 = new OutboxRecord("job", UUID.randomUUID(), "job.queued", "{\"a\":1}");
        OutboxRecord event2 = new OutboxRecord("job", UUID.randomUUID(), "job.queued", "{\"a\":2}");
        OutboxRecord event3 = new OutboxRecord("job", UUID.randomUUID(), "job.queued", "{\"a\":3}");

        when(outboxRepository.getFiveEvents()).thenReturn(List.of(event1, event2, event3));

        CompletableFuture<SendResult<Object, Object>> ok1 =
                CompletableFuture.completedFuture(null);

        CompletableFuture<SendResult<Object, Object>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("send failed"));

        CompletableFuture<SendResult<Object, Object>> ok3 =
                CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(eq("jobs.queue"), eq(event1))).thenReturn(ok1);
        when(kafkaTemplate.send(eq("jobs.queue"), eq(event2))).thenReturn(failed);
        when(kafkaTemplate.send(eq("jobs.queue"), eq(event3))).thenReturn(ok3);

        jobsScheduler.send();

        verify(outboxRepository).saveEvents(outboxRecordsCaptor.capture());

        List<OutboxRecord> saved = outboxRecordsCaptor.getValue();
        assertEquals(2, saved.size());
        assertEquals(event1, saved.get(0));
        assertEquals(event3, saved.get(1));

        verify(kafkaTemplate).send("jobs.queue", event1);
        verify(kafkaTemplate).send("jobs.queue", event2);
        verify(kafkaTemplate).send("jobs.queue", event3);
    }
}
