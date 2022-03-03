package com.igorgorbunov3333.timer.service.pomodoro.synchronization;

import com.igorgorbunov3333.timer.model.dto.SynchronizationJobDto;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.enums.SynchronizationAction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@AllArgsConstructor
public class PomodoroSynchronizationScheduler {

    private final BlockingQueue<SynchronizationJobDto> queue = new LinkedBlockingDeque<>(10);

    @SneakyThrows
    public SynchronizationJobDto take() {
        return queue.take();
    }

    @SneakyThrows
    public void addUpdateJob() {
        SynchronizationJobDto job = new SynchronizationJobDto(SynchronizationAction.UPDATE, null);
        queue.put(job);
    }

    @SneakyThrows
    public void addRemovalJob(@NonNull Long pomodoroId) {
        SynchronizationJobDto job = new SynchronizationJobDto(SynchronizationAction.REMOVE, null);
        queue.put(job);
    }

}
