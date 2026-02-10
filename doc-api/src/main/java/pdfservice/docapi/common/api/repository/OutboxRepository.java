package pdfservice.docapi.common.api.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pdfservice.docapi.common.api.dto.OutboxRecord;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
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

    public List<OutboxRecord> getFiveEvents() {
        return jdbcTemplate.query(""" 
        SELECT aggregate_type, aggregate_id, event_type, payload
        FROM outbox
        WHERE published_at IS NULL ORDER BY created_at LIMIT 5
        """,
                (rs, rowNum) -> new OutboxRecord(
                        rs.getString("aggregate_type"),
                        rs.getObject("aggregate_id", UUID.class),
                        rs.getString("event_type"),
                        rs.getString("payload"))
        );
    }

    @Transactional
    public void saveEvents(List<OutboxRecord> events) {
        if (events.isEmpty()){
            return;
        }
        jdbcTemplate.batchUpdate("""
        UPDATE outbox SET published_at = now()
        WHERE aggregate_id = ? AND published_at IS NULL
""", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, events.get(i).aggregateId());
            }

            @Override
            public int getBatchSize() {
                return events.size();
            }
        });
    }
}
