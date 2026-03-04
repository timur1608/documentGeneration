package pdfservice.docapi.common.api.dto;

import tools.jackson.databind.JsonNode;

public record GetJobResponseDto(
        JsonNode payload
) {
}
