package pdfservice.docapi.common.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import pdfservice.docapi.IntegrationTestBase;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TemplateControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTemplate_returnsSeededTemplate() throws Exception {
        UUID templateVersionId = UUID.fromString("7a4d0c3b-9e7b-4db9-9c1c-6a0c1f2e3d4a");

        mockMvc.perform(get("/templates/{templateVersionId}", templateVersionId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invoice</h1>")));
    }

    @Test
    void getTemplate_returnsBadRequestWhenMissing() throws Exception {
        UUID templateVersionId = UUID.randomUUID();

        mockMvc.perform(get("/templates/{templateVersionId}", templateVersionId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No templateVersionId found"));
    }
}
