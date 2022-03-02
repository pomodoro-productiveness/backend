create table pomodoro_synchronization_info(
    id bigserial primary key,
    time timestamp not null,
    synchronized_successfully boolean not null,
    synchronization_result text,
    synchronization_error text
);
