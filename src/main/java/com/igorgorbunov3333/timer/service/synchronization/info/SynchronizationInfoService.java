package com.igorgorbunov3333.timer.service.synchronization.info;

import com.igorgorbunov3333.timer.model.entity.SynchronizationInfo;
import com.igorgorbunov3333.timer.model.entity.enums.SynchronizationResult;

import java.util.Optional;

public interface SynchronizationInfoService {

    void save(Boolean successfullySynchronized,
              SynchronizationResult synchronizationResult,
              String synchronizationError);

    Optional<SynchronizationInfo> getLatestPomodoroSynchronizationInfo();

}
