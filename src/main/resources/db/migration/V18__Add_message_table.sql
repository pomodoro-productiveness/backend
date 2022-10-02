create table message(
    id bigserial primary key,
    date timestamp not null,
    message_period text not null
);
