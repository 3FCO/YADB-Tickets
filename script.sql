create table if not exists server_info
(
    server_id    bigint not null
        constraint yadbtickets_pk
            primary key,
    category_id  bigint,
    channel_id   bigint,
    support_id   bigint,
    moderator_id bigint
);

create unique index if not exists yadbtickets_category_id_uindex
    on server_info (category_id);

create unique index if not exists yadbtickets_channel_id_uindex
    on server_info (channel_id);

create unique index if not exists yadbtickets_moderator_id_uindex
    on server_info (moderator_id);

create unique index if not exists yadbtickets_server_id_uindex
    on server_info (server_id);

create unique index if not exists yadbtickets_support_id_uindex
    on server_info (support_id);

create table if not exists button_interaction
(
    button_id         serial
        constraint button_interaction_pk
            primary key,
    event_type        varchar not null,
    event_information varchar not null
);

create unique index if not exists button_interaction_button_id_uindex
    on button_interaction (button_id);

create table if not exists tickets
(
    id        serial
        constraint tickets_pk
            primary key,
    author    bigint               not null,
    supporter bigint,
    logs      varchar,
    active    boolean default true not null
);

create unique index if not exists tickets_id_uindex
    on tickets (id);

create unique index if not exists tickets_logs_uindex
    on tickets (logs);


