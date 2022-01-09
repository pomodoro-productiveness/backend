create table pomodoro(
    id bigserial primary key,
    start_time timestamp not null,
    end_time timestamp not null
);