create table channel
(
    id         BIGSERIAL not null primary key,
    service    varchar(32) not null,
    channel_id varchar(32) not null,
    guild_id   varchar(32),
    created_at timestamp not null,
    updated_at timestamp not null,
    UNIQUE(service, channel_id, guild_id)
);