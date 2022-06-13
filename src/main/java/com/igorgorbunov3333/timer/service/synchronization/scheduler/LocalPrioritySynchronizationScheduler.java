package com.igorgorbunov3333.timer.service.synchronization.scheduler;

import com.igorgorbunov3333.timer.service.synchronization.priority.local.LocalPrioritySynchronizer;
import com.igorgorbunov3333.timer.service.synchronization.toggler.LocalPrioritySynchronizationToggler;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LocalPrioritySynchronizationScheduler {

    private final LocalPrioritySynchronizationToggler localPrioritySynchronizationToggler;
    private final LocalPrioritySynchronizer synchronizer;

    @Scheduled(fixedDelay = 1000)
    public void checkTaskForLocalPrioritySynchronization() {
        if (localPrioritySynchronizationToggler.isNeedToSynchronize()) {
            localPrioritySynchronizationToggler.synchronizationStarted();
            synchronizer.synchronize();
        }
    }

}

