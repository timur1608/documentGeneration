package pdfservice.docapi.common.api.dto;

import java.util.UUID;

public record WebHookResponse(
    UUID jobId,
    String status,
    String s3Url
) {
}
