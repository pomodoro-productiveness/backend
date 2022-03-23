package com.igorgorbunov3333.timer.service.pomodoro.synchronization;

import com.igorgorbunov3333.timer.model.dto.SynchronizationJobDto;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.enums.SynchronizationAction;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class SynchronizationJobProcessor {

    private final PomodoroSynchronizerService pomodoroSynchronizerService;
    private final PomodoroSynchronizationScheduler pomodoroSynchronizationScheduler;

    @Async
    public void run() {
        try {
            while (true) {
                SynchronizationJobDto synchronizationJobDto = pomodoroSynchronizationScheduler.take();
                Thread.sleep(10000);
                if (SynchronizationAction.UPDATE.equals(synchronizationJobDto.getSynchronizationAction())) {
                    pomodoroSynchronizerService.synchronize(synchronizationJobDto.getTimestamp());
                } else {
                    LocalDateTime timestamp = synchronizationJobDto.getTimestamp();
                    pomodoroSynchronizerService.synchronizeAfterRemovingPomodoro(timestamp);
                }
            }
        } catch (Exception e) {
            System.out.println("Pomodoro was not synchronized due to the error");
            e.printStackTrace();
        }
    }

}
