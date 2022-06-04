package com.igorgorbunov3333.timer.service.synchronization.priority.local;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@AllArgsConstructor
public class LocalPrioritySynchronizationToggler {

    private final AtomicBoolean flag = new AtomicBoolean(Boolean.FALSE);

    public void toggleToSynchronize() {
        flag.set(true);
    }

    public void toggleToSynchronizationStarted() {
        flag.set(false);
    }

    public boolean needToSynchronize() {
        return flag.get();
    }

}
