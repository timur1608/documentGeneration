create table if not exists outbox_finished_jobs (
                                      id bigserial primary key,
                                      aggregate_id uuid not null,
                                      payload text not null,
                                      created_at timestamptz not null default now(),
    locked_until timestamptz,
    published_at timestamptz,
    unique (aggregate_id)
    );

create index if not exists idx_outbox_finished_jobs_pub on outbox_finished_jobs(published_at, id);