alter table pomodoro_tag drop column parent_id;

create table pomodoro_pomodoro_tag (
    pomodoro_id bigint,
    tag_id bigint
);
