package com.igorgorbunov3333.timer.service.synchronization.scheduler;

import com.igorgorbunov3333.timer.service.synchronization.priority.local.LocalPrioritySynchronizationToggler;
import com.igorgorbunov3333.timer.service.synchronization.SynchronizationCoordinator;
import com.igorgorbunov3333.timer.service.synchronization.enums.SynchronizationPriorityType;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LocalPrioritySynchronizationScheduler {

    private final LocalPrioritySynchronizationToggler localPrioritySynchronizationToggler;
    private final SynchronizationCoordinator synchronizationCoordinator;

    @Scheduled(fixedDelay = 1000)
    public void checkTaskForLocalPrioritySynchronization() {
        if (localPrioritySynchronizationToggler.needToSynchronize()) {
            localPrioritySynchronizationToggler.toggleToSynchronizationStarted();
            synchronizationCoordinator.synchronize(SynchronizationPriorityType.LOCAL);
        }
    }

}

