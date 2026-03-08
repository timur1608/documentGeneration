package pdfservice.docapi.common.api.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pdfservice.docapi.common.api.dto.CreateJobRequestDto;
import pdfservice.docapi.common.api.service.JobService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobController.class)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @Test
    void createJob_returnsQueuedResponse() throws Exception {
        UUID tenantId = UUID.randomUUID();
        UUID templateVersionId = UUID.randomUUID();
        String requestId = "request-1";
        String webhookUrl = "https://example.com/hook";
        UUID jobId = UUID.randomUUID();

        when(jobService.createJob(any(CreateJobRequestDto.class))).thenReturn(jobId);

        String body = """
                {
                  "tenantId": "%s",
                  "templateVersionId": "%s",
                  "requestId": "%s",
                  "payload": { "foo": "bar" },
                  "webhookUrl": "%s"
                }
                """.formatted(tenantId, templateVersionId, requestId, webhookUrl);

        mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(jobId.toString()))
                .andExpect(jsonPath("$.status").value("QUEUED"));

        ArgumentCaptor<CreateJobRequestDto> captor = ArgumentCaptor.forClass(CreateJobRequestDto.class);
        verify(jobService).createJob(captor.capture());

        CreateJobRequestDto dto = captor.getValue();
        assertEquals(tenantId, dto.tenantId());
        assertEquals(templateVersionId, dto.templateVersionId());
        assertEquals(requestId, dto.requestId());
        assertEquals(webhookUrl, dto.webhookUrl());
        assertEquals("bar", dto.payload().get("foo").asString());
    }

    @Test
    void findJob_returnsPayload() throws Exception {
        UUID jobId = UUID.randomUUID();
        JsonNode payload = new ObjectMapper().readTree("{\"x\":1}");

        when(jobService.getJobPayload(jobId)).thenReturn(payload);

        mockMvc.perform(get("/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.x").value(1));
    }
}