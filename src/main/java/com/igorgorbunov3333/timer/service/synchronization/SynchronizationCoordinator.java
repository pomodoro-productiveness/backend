package com.igorgorbunov3333.timer.service.synchronization;

import com.igorgorbunov3333.timer.service.synchronization.enums.SynchronizationPriorityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SynchronizationCoordinator {

    private final Map<SynchronizationPriorityType, Synchronizer> synchronizationTypeBySynchronizer;

    @Autowired
    public SynchronizationCoordinator(List<Synchronizer> synchronizers) {
        synchronizationTypeBySynchronizer = synchronizers.stream()
                .collect(Collectors.toMap(Synchronizer::synchronizationType, Function.identity()));
    }

    public void synchronize(SynchronizationPriorityType synchronizationPriorityType) {
        Synchronizer synchronizer = synchronizationTypeBySynchronizer.get(synchronizationPriorityType);
        if (synchronizationPriorityType != null) {
            synchronizer.synchronize();
        }
    }

}
