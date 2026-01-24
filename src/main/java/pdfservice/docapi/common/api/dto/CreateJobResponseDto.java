package pdfservice.docapi.common.api.dto;

import java.util.UUID;

public record CreateJobResponseDto (
    UUID jobId,
    String status
)
{}
