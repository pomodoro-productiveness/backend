package com.igorgorbunov3333.timer.service.pomodoro.synchronization;

import com.igorgorbunov3333.timer.model.dto.SynchronizationJobDto;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.enums.SynchronizationAction;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Scope("prototype")
@AllArgsConstructor
public class SynchronizationJobProcessor implements Runnable {

    private final PomodoroSynchronizerService pomodoroSynchronizerService;
    private final PomodoroSynchronizationScheduler pomodoroSynchronizationScheduler;

    @Override
    @SneakyThrows
    public void run() {
        try {
            while (true) {
                SynchronizationJobDto synchronizationJobDto = pomodoroSynchronizationScheduler.take();
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
