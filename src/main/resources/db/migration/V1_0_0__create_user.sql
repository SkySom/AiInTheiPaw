create table "user"
(
    id           BIGSERIAL not null primary key,
    created_date timestamp not null,
    updated_date timestamp not null
);

create table user_source
(
    id           BIGSERIAL not null primary key,
    service      varchar(32) not null,
    service_id   varchar(64) not null,
    created_date timestamp   not null,
    updated_date timestamp   not null,
    user_id      BIGSERIAL   not null references "user",
    unique (service, service_id)
);