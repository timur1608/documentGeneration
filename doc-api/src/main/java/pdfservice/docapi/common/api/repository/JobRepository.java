package pdfservice.docapi.common.api.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JobRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UUID createJobOrFindExisting(UUID id, UUID tenant_id, UUID template_version_id, String request_id, String payload) {
        return jdbcTemplate.queryForObject("""
                INSERT INTO jobs(id, tenant_id, template_version_id, request_id, payload, status)
                VALUES (?, ?, ?, ?, cast(? as jsonb), 'QUEUED')
                ON CONFLICT (tenant_id, request_id) DO UPDATE
                SET request_id = EXCLUDED.request_id
                RETURNING ID
                """, UUID.class, id, tenant_id, template_version_id, request_id, payload);
    }
}
