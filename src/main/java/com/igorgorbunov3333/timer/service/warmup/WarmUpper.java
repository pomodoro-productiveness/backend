package com.igorgorbunov3333.timer.service.warmup;

import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroSynchronizerService;
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
        try {
            pomodoroSynchronizerService.synchronize();
            System.out.println("Pomodoros successfully synchronized");
        } catch (Exception e) {
            System.out.println("Pomodoro was not synchronized due to the error");
            e.printStackTrace();
        }
    }

}
