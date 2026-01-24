create table if not exists template_groups (
                                               id uuid primary key,
                                               tenant_id uuid not null,
                                               name text not null,
                                               engine text not null default 'freemarker',
                                               created_at timestamptz not null default now(),
    unique (tenant_id, name)
    );

create table if not exists template_versions (
                                                 id uuid primary key,
                                                 group_id uuid not null references template_groups(id) on delete cascade,
    version int not null,
    content text not null,
    created_at timestamptz not null default now(),
    unique (group_id, version)
    );

create table if not exists jobs (
                                    id uuid primary key,
                                    tenant_id uuid not null,
                                    template_version_id uuid not null references template_versions(id),
    request_id text not null,
    payload jsonb not null,
    status text not null check (status in ('QUEUED','COMPLETED','FAILED')),
    result_key text,
    error text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (tenant_id, request_id)
    );

create index if not exists idx_jobs_tenant_created_at on jobs(tenant_id, created_at desc);
create index if not exists idx_jobs_template_version on jobs(template_version_id);

create table if not exists outbox (
                                      id bigserial primary key,
                                      aggregate_type text not null,
                                      aggregate_id uuid not null,
                                      event_type text not null,
                                      payload jsonb not null,
                                      created_at timestamptz not null default now(),
    published_at timestamptz
    );

create index if not exists idx_outbox_pub on outbox(published_at, id);

create table if not exists processed_events (
                                                consumer_name text not null,
                                                event_id text not null,
                                                processed_at timestamptz not null default now(),
    primary key (consumer_name, event_id)
    );
