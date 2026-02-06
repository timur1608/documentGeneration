package pdfservice.docapi.common.api.dto;

import java.util.UUID;

public record OutboxEvent(
    String aggregatedType,
    UUID aggregateId,
    String eventType,
    String payload
) {
}
