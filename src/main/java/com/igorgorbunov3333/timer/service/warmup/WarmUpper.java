package com.igorgorbunov3333.timer.service.warmup;

import com.igorgorbunov3333.timer.service.synchronization.priority.remote.RemotePrioritySynchronizer;
import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WarmUpper {

    private final RemotePrioritySynchronizer synchronizer;

    @EventListener({ContextRefreshedEvent.class})
    void onStartup() {
        synchronizer.synchronize();
    }

}
