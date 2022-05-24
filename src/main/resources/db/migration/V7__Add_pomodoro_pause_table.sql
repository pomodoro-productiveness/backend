create table pomodoro_pause(
    id bigserial primary key,
    start_time timestamptz not null,
    end_time timestamptz not null,
    pomodoro_id bigint,
    foreign key (pomodoro_id) references pomodoro(id)
);
