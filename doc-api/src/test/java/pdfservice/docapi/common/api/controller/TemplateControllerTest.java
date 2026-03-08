package pdfservice.docapi.common.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pdfservice.docapi.common.api.repository.TemplateRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TemplateController.class)
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TemplateRepository templateRepository;

    @Test
    void getTemplate_returnsContent() throws Exception {
        UUID templateId = UUID.randomUUID();
        String content = "<html>template</html>";

        when(templateRepository.getContent(eq(templateId))).thenReturn(Optional.of(content));

        mockMvc.perform(get("/templates/{templateVersionId}", templateId))
                .andExpect(status().isOk())
                .andExpect(content().string(content));
    }

    @Test
    void getTemplate_returnsBadRequestWhenMissing() throws Exception {
        UUID templateId = UUID.randomUUID();

        when(templateRepository.getContent(eq(templateId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/templates/{templateVersionId}", templateId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No templateVersionId found"));
    }
}
