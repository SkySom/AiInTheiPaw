create table "user"
(
    id         BIGSERIAL not null primary key,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table user_source
(
    id              BIGSERIAL   not null primary key,
    service         varchar(32) not null,
    service_user_id varchar(64) not null,
    created_at      timestamp   not null,
    updated_at      timestamp   not null,
    user_id         BIGSERIAL   not null references "user",
    unique (service, service_user_id)
);