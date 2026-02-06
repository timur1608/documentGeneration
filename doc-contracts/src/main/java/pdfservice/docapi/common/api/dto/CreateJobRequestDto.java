package pdfservice.docapi.common.api.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record CreateJobRequestDto(
    @NotNull UUID tenantId,
    @NotNull UUID templateVersionId,
    @NotBlank @Size(max = 128) String requestId,
    @NotNull JsonNode payload,
    @Nullable String webhookUrl
) {}
