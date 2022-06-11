package com.igorgorbunov3333.timer.service.warmup;

import com.igorgorbunov3333.timer.service.dayoff.DayOffSynchronizer;
import com.igorgorbunov3333.timer.service.synchronization.SynchronizationCoordinator;
import com.igorgorbunov3333.timer.service.synchronization.enums.SynchronizationPriorityType;
import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WarmUpper {

    private final SynchronizationCoordinator synchronizationCoordinator;
    private final DayOffSynchronizer dayOffSynchronizer;

    @EventListener({ContextRefreshedEvent.class})
    void onStartup() {
        synchronizationCoordinator.synchronize(SynchronizationPriorityType.REMOTE);
        dayOffSynchronizer.synchronize();
    }

}
