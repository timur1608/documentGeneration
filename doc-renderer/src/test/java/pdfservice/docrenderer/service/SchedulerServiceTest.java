package pdfservice.docrenderer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docrenderer.repository.OutboxFinishedJobsRepository;

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
class SchedulerServiceTest {

    @Mock
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Mock
    private OutboxFinishedJobsRepository repository;

    @InjectMocks
    private SchedulerService schedulerService;

    @Captor
    private ArgumentCaptor<List<OutboxFinishedJobRecord>> recordsCaptor;

    @Test
    void readEventsFromOutbox_noEvents_savesEmptyList() {
        when(repository.getFiveEvents()).thenReturn(List.of());

        schedulerService.readEventsFromOutbox();

        verify(kafkaTemplate, never()).send(anyString(), any());

        verify(repository).saveEvents(recordsCaptor.capture());
        assertTrue(recordsCaptor.getValue().isEmpty());
    }

    @Test
    void readEventsFromOutbox_mixedResults_savesOnlySuccessful() {
        OutboxFinishedJobRecord e1 = new OutboxFinishedJobRecord(UUID.randomUUID(), "key1");
        OutboxFinishedJobRecord e2 = new OutboxFinishedJobRecord(UUID.randomUUID(), "key2");

        when(repository.getFiveEvents()).thenReturn(List.of(e1, e2));

        CompletableFuture<SendResult<Object, Object>> ok = CompletableFuture.completedFuture(null);
        CompletableFuture<SendResult<Object, Object>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("send failed"));

        when(kafkaTemplate.send(eq("jobs.result"), eq(e1))).thenReturn(ok);
        when(kafkaTemplate.send(eq("jobs.result"), eq(e2))).thenReturn(failed);

        schedulerService.readEventsFromOutbox();

        verify(repository).saveEvents(recordsCaptor.capture());

        List<OutboxFinishedJobRecord> saved = recordsCaptor.getValue();
        assertEquals(1, saved.size());
        assertEquals(e1, saved.get(0));
    }
}