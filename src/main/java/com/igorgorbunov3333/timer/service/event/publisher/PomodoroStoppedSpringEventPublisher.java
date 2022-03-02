package com.igorgorbunov3333.timer.service.event.publisher;

import com.igorgorbunov3333.timer.model.event.PomodoroStoppedSpringEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PomodoroStoppedSpringEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publish(Integer pomodoroDuration) {
        PomodoroStoppedSpringEvent event = new PomodoroStoppedSpringEvent(this, pomodoroDuration);
        applicationEventPublisher.publishEvent(event);
    }

}
