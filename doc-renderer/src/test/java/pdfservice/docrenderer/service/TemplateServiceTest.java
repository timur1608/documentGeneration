package pdfservice.docrenderer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TemplateService templateService;

    @Test
    void getTemplateById_returnsContent() {
        UUID templateId = UUID.randomUUID();
        String content = "<html>template</html>";

        when(restTemplate.getForObject(
                eq("http://localhost:8080/templates/{id}"),
                eq(String.class),
                eq(templateId)
        )).thenReturn(content);

        String result = templateService.getTemplateById(templateId);

        assertEquals(content, result);
    }

    @Test
    void getTemplateById_returnsFallbackOnError() {
        UUID templateId = UUID.randomUUID();

        when(restTemplate.getForObject(
                eq("http://localhost:8080/templates/{id}"),
                eq(String.class),
                eq(templateId)
        )).thenThrow(new RuntimeException("fail"));

        String result = templateService.getTemplateById(templateId);

        assertEquals("No such template", result);
    }
}
