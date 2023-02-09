create table if not exists users
(
    id    bigint generated by default as identity,
    name  varchar(100),
    email varchar(50),
    constraint pk_user primary key (id),
    constraint uq_user_email unique (email)
);
create table if not exists requests
(
    id           bigint generated by default as identity
        constraint pk_request primary key,
    description  varchar(4000),
    requestor_id bigint
        constraint requests_users_id_fk
            references users,
    created      timestamp without time zone
);
create table if not exists items
(
    id          bigint generated by default as identity,
    name        varchar(100),
    description varchar(4000),
    available   boolean,
    owner_id    bigint
        constraint items_users_id_fk
            references users,
    constraint pk_item primary key (id),
    request_id   bigint
        constraint items_requests_id_fk
            references requests
);
create table if not exists bookings
(
    id         bigint generated by default as identity
        constraint pk_booking
            primary key,
    start_date timestamp without time zone,
    end_date   timestamp without time zone,
    item_id    bigint
        constraint bookings_items_id_fk
            references items,
    booker_id  bigint
        constraint bookings_users_id_fk
            references users,
    status     varchar
);
create table if not exists comments
(
    id        bigint generated by default as identity
        constraint pk_comment primary key,
    text      varchar(500)  ,
    item_id   bigint
        constraint comments_items__fk
            references items,
    author_id bigint
        constraint comments_users_id_fk
            references users,
    created   timestamp without time zone
);
create table if not exists requests
(
    id           bigint generated by default as identity
        constraint pk_request primary key,
    description  varchar(4000),
    requestor_id bigint
        constraint requests_users_id_fk
            references users,
    created      timestamp without time zone
);