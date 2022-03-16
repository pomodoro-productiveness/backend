package com.igorgorbunov3333.timer.service.pomodoro.synchronization;

import com.igorgorbunov3333.timer.model.dto.SynchronizationJobDto;
import com.igorgorbunov3333.timer.service.pomodoro.synchronization.enums.SynchronizationAction;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
        SynchronizationJobDto job = new SynchronizationJobDto(SynchronizationAction.UPDATE, LocalDateTime.now());
        queue.put(job);
    }

    @SneakyThrows
    public void addRemovalJob() {
        SynchronizationJobDto job = new SynchronizationJobDto(SynchronizationAction.REMOVE, LocalDateTime.now());
        queue.put(job);
    }

}
