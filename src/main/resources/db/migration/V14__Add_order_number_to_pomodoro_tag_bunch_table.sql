alter table pomodoro_tag_bunch add column order_number bigint;
update pomodoro_tag_bunch set order_number = 1 where id > 0;
alter table pomodoro_tag_bunch alter column order_number set not null;
