package com.igorgorbunov3333.timer.service.warmup;

import com.igorgorbunov3333.timer.service.pomodoro.synchronization.PomodoroSynchronizationScheduler;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.SynchronizationJobProcessor;
import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WarmUpper {

    private final SynchronizationJobProcessor synchronizationJobProcessor;
    private final PomodoroSynchronizationScheduler pomodoroSynchronizationScheduler;

    @EventListener({ContextRefreshedEvent.class})
    void onStartup() {
        synchronizationJobProcessor.run();
        pomodoroSynchronizationScheduler.addUpdateJob();
    }

}
