package com.igorgorbunov3333.timer.model.event;

import org.springframework.context.ApplicationEvent;

public class PomodoroStoppedSpringEvent extends ApplicationEvent {

    private final Integer pomodoroDuration;

    public PomodoroStoppedSpringEvent(Object source, Integer pomodoroDuration) {
        super(source);
        this.pomodoroDuration = pomodoroDuration;
    }

    public Integer getPomodoroDuration() {
        return pomodoroDuration;
    }

}
