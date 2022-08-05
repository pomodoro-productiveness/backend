alter table pomodoro_tag_bunch rename to pomodoro_tag_group;
alter table pomodoro_tag_bunch_tag rename to pomodoro_tag_group_tag;
alter table pomodoro_tag_group_tag rename column pomodoro_tag_bunch_id to pomodoro_tag_group_id;
