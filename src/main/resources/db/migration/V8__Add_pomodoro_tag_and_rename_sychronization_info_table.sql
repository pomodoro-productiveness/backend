create table pomodoro_tag(
    id bigserial primary key,
    name text not null unique,
    parent_id bigint,
    removed boolean
);

alter table pomodoro add tag_id bigint;
alter table pomodoro add constraint fk_tag_id foreign key (tag_id) references pomodoro_tag(id);
alter table pomodoro_synchronization_info rename to synchronization_info;
