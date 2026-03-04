ALTER TABLE outbox
    ADD CONSTRAINT outbox_aggregate_id_uniq UNIQUE (aggregate_id);