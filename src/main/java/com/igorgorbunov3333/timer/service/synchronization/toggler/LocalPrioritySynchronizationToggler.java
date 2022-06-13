package com.igorgorbunov3333.timer.service.synchronization.toggler;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LocalPrioritySynchronizationToggler {

    private final AtomicBoolean flag = new AtomicBoolean(Boolean.FALSE);

    public void synchronize() {
        flag.set(true);
    }

    public void synchronizationStarted() {
        flag.set(false);
    }

    public boolean isNeedToSynchronize() {
        return flag.get();
    }

}
