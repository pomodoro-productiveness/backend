package com.igorgorbunov3333.timer.service.pomodoro.synchronization;

import com.igorgorbunov3333.timer.model.entity.PomodoroSynchronizationInfo;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;

import java.util.Optional;

public interface PomodoroInfoSynchronizationService {

    void save(Boolean successfullySynchronized,
              SynchronizationResult synchronizationResult,
              String synchronizationError);

    Optional<PomodoroSynchronizationInfo> getLatestPomodoroSynchronizationInfo();

}
