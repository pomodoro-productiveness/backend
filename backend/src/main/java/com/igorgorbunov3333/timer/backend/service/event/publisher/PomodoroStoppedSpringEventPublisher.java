package com.igorgorbunov3333.timer.backend.service.event.publisher;

import com.igorgorbunov3333.timer.backend.model.event.PomodoroStoppedSpringEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PomodoroStoppedSpringEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(Integer pomodoroDuration) {
        PomodoroStoppedSpringEvent event = new PomodoroStoppedSpringEvent(this, pomodoroDuration);
        applicationEventPublisher.publishEvent(event);
    }

}
