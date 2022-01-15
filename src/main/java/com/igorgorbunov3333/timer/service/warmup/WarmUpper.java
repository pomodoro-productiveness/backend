package com.igorgorbunov3333.timer.service.warmup;

import com.igorgorbunov3333.timer.service.pomodoro.PomodoroSynchronizerService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WarmUpper {

    private final PomodoroSynchronizerService pomodoroSynchronizerService;

    @EventListener({ContextRefreshedEvent.class})
    void onStartup() {
        pomodoroSynchronizerService.synchronize();
    }

}
