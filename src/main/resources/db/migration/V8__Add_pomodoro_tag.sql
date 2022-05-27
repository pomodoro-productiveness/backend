create table pomodoro_tag(
    id bigserial primary key,
    name text not null,
    parent_id bigint
);

alter table pomodoro add tag_id bigint;
alter table pomodoro add constraint fk_tag_id foreign key (tag_id) references pomodoro_tag(id);
