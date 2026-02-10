package pdfservice.docapi.common.api.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OutboxEvent(
        String aggregatedType,
        UUID aggregateId,
        String eventType,
        OutboxPayload payload
) {
}
