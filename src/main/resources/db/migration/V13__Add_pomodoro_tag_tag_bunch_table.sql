create table pomodoro_tag_bunch (
    id bigserial primary key
);

create table pomodoro_tag_bunch_tag (
    pomodoro_tag_bunch_id bigint,
    pomodoro_tag_id bigint
);
