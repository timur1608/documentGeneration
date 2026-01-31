package pdfservice.docapi.common.api.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OutboxRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OutboxRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void publishJob() {
        jdbcTemplate.update("""
        INSERT INTO outbox (aggregate_type, aggregate_id, event_type, payload, created_at)
        VALUES (?, ?, ?, ?, ?)
""");
    }
}
