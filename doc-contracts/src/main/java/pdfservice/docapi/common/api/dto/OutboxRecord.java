package pdfservice.docapi.common.api.dto;

import java.util.UUID;

public record OutboxRecord(
    String aggregatedType,
    UUID aggregateId,
    String eventType,
    String payload
) {
}
