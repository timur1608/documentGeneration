package pdfservice.docapi.common.api.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public class OutboxRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OutboxRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void publishJob(String aggregate_type, UUID aggregate_id, String event_type, String payload) {
        jdbcTemplate.update("""
        INSERT INTO outbox (aggregate_type, aggregate_id, event_type, payload)
        VALUES (?, ?, ?, cast(? as jsonb)) ON CONFLICT DO NOTHING
        """, aggregate_type, aggregate_id, event_type, payload);
    }
}
