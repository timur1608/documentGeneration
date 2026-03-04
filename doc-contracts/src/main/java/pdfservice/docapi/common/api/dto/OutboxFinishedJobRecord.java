package pdfservice.docapi.common.api.dto;

import java.util.UUID;

public record OutboxFinishedJobRecord(
        UUID jobId,
        String key
) {
}
