package pdfservice.docapi.common.api.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TemplateRepository templateRepository;

    @Test
    void getContent_returnsValueWhenExists() {
        UUID templateId = UUID.randomUUID();
        String content = "<html>template</html>";

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(templateId)))
                .thenReturn(content);

        Optional<String> result = templateRepository.getContent(templateId);

        assertTrue(result.isPresent());
        assertEquals(content, result.get());
    }

    @Test
    void getContent_returnsEmptyWhenException() {
        UUID templateId = UUID.randomUUID();

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(templateId)))
                .thenThrow(new RuntimeException("db error"));

        Optional<String> result = templateRepository.getContent(templateId);

        assertTrue(result.isEmpty());
    }
}
