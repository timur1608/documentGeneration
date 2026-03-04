package pdfservice.docrenderer.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class OutboxFinishedJobsRepository {
    private final JdbcTemplate jdbcTemplate;
    public OutboxFinishedJobsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveEvent(OutboxFinishedJobRecord event){
        jdbcTemplate.update("""
            INSERT INTO outbox_finished_jobs (aggregate_id, payload)
            VALUES (?, ?) ON CONFLICT DO NOTHING
""", event.jobId(), event.key()
        );
    }
    @Transactional
    public List<OutboxFinishedJobRecord> getFiveEvents(){
        return jdbcTemplate.query(
                """
                        WITH selected AS 
                            (SELECT aggregate_id, payload
                            FROM outbox_finished_jobs
                            WHERE (locked_until IS NULL OR locked_until < now())
                            AND published_at IS NULL
                            ORDER BY created_at 
                            LIMIT 5 FOR UPDATE SKIP LOCKED)
                        UPDATE outbox_finished_jobs o
                        SET locked_until = now() + interval '1 minute'
                        FROM selected
                        WHERE o.aggregate_id = selected.aggregate_id
                        RETURNING o.aggregate_id, o.payload
                        """,
                (rs, rowNum) -> new OutboxFinishedJobRecord (
                        rs.getObject("aggregate_id", UUID.class),
                        rs.getString("payload")
                )
        );
    }

    @Transactional
    public void saveEvents(List<OutboxFinishedJobRecord> events) {
        if (events.isEmpty()){
            return;
        }
        jdbcTemplate.batchUpdate("""
        UPDATE outbox_finished_jobs
        SET published_at = now()
        WHERE aggregate_id = ? AND published_at IS NULL
""",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, events.get(i).jobId());
                    }

                    @Override
                    public int getBatchSize() {
                        return events.size();
                    }
                });
    }
}
