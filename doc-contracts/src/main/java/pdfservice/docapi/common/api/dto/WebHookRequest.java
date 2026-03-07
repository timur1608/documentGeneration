package pdfservice.docapi.common.api.dto;

import java.util.UUID;

public record WebHookRequest(
    UUID jobId,
    String status,
    String s3Url
) {
}
