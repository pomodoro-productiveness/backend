package com.igorgorbunov3333.timer.model.dto;

import com.igorgorbunov3333.timer.service.pomodoro.synchronization.enums.SynchronizationAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SynchronizationJobDto {

    private final SynchronizationAction synchronizationAction;
    private final LocalDateTime timestamp;

}
