alter table pomodoro add column saved_automatically boolean;
update pomodoro set saved_automatically = false where id > 0;
alter table pomodoro alter column saved_automatically set not null;
