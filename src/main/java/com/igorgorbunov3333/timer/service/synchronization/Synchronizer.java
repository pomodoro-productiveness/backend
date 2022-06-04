package com.igorgorbunov3333.timer.service.synchronization;

import com.igorgorbunov3333.timer.service.synchronization.enums.SynchronizationPriorityType;

public interface Synchronizer {

    void synchronize();

    SynchronizationPriorityType synchronizationType();

}
