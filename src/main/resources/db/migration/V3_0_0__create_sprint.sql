create table sprint
(
    id         BIGSERIAL   not null primary key,
    channel_id BIGSERIAL   not null references channel (id),
    status     varchar(16) not null,
    created_at timestamp   not null,
    updated_at timestamp   not null
);

create table sprint_entry
(
    id             BIGSERIAL not null primary key,
    user_id        BIGSERIAL not null references "user" (id),
    sprint_id      BIGSERIAL not null references sprint (id),
    starting_count INT       not null,
    ending_count   INT,
    created_at     timestamp not null,
    updated_at     timestamp not null
);

create table sprint_status
(
    id              BIGSERIAL   not null primary key,
    sprint_id       BIGSERIAL   not null references sprint (id),
    new_status      varchar(16) not null,
    previous_status varchar(16) not null,
    next_update     timestamp,
    created_at      timestamp   not null,
    updated_at      timestamp   not null
);