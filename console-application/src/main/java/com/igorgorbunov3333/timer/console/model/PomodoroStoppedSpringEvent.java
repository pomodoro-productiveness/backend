package com.igorgorbunov3333.timer.console.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PomodoroStoppedSpringEvent extends ApplicationEvent {

    private final Integer pomodoroDuration;

    public PomodoroStoppedSpringEvent(Object source, Integer pomodoroDuration) {
        super(source);
        this.pomodoroDuration = pomodoroDuration;
    }

}