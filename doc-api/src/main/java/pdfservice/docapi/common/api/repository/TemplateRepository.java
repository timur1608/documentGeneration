package pdfservice.docapi.common.api.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TemplateRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TemplateRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<String> getContent(UUID templateVersionId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                SELECT content FROM template_versions
                WHERE id = ?
                """, String.class, templateVersionId));
        } catch (Exception e){
            return Optional.empty();
        }
    }
}
