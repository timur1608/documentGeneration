package pdfservice.docapi.common.api.dto;

import java.util.UUID;

public record OutboxPayload(
    UUID jobId,
    UUID tenantId,
    String requestId,
    UUID templateVersionId
) {

}
