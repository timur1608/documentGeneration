ALTER TABLE outbox
ADD COLUMN locked_until timestamptz;