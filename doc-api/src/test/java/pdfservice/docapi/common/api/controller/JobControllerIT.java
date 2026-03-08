package pdfservice.docapi.common.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pdfservice.docapi.IntegrationTestBase;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JobControllerIT extends IntegrationTestBase {

    private static final UUID TEMPLATE_VERSION_ID =
            UUID.fromString("2b9c7b1e-2e6f-4b5f-9f8d-0e1c2d3a4b5c");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createJob_persistsJobAndOutbox_andGetJobReturnsPayload() throws Exception {
        UUID tenantId = UUID.randomUUID();
        String requestId = "request-it-1";
        String webhookUrl = "https://example.com/hook";

        String body = """
                {
                  "tenantId": "%s",
                  "templateVersionId": "%s",
                  "requestId": "%s",
                  "payload": { "foo": "bar" },
                  "webhookUrl": "%s"
                }
                """.formatted(tenantId, TEMPLATE_VERSION_ID, requestId, webhookUrl);

        MvcResult result = mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("QUEUED"))
                .andReturn();

        JsonNode responseJson = objectMapper.readTree(result.getResponse().getContentAsString());
        UUID jobId = UUID.fromString(responseJson.get("jobId").asString());

        String status = jdbcTemplate.queryForObject(
                "select status from jobs where id = ?",
                String.class,
                jobId
        );
        assertEquals("QUEUED", status);

        String storedPayload = jdbcTemplate.queryForObject(
                "select payload from jobs where id = ?",
                String.class,
                jobId
        );
        JsonNode storedPayloadJson = objectMapper.readTree(storedPayload);
        assertEquals("bar", storedPayloadJson.get("foo").asString());

        Long outboxCount = jdbcTemplate.queryForObject(
                "select count(*) from outbox where aggregate_id = ?",
                Long.class,
                jobId
        );
        assertEquals(1L, outboxCount);

        mockMvc.perform(get("/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.foo").value("bar"));
    }
}
