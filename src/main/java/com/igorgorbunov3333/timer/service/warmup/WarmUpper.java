package com.igorgorbunov3333.timer.service.warmup;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroMetadataDto;
import com.igorgorbunov3333.timer.service.pomodoro.calendar.PomodoroCalendarEventProcessor;
import com.igorgorbunov3333.timer.service.pomodoro.provider.remote.RemotePomodoroDataService;
import com.igorgorbunov3333.timer.service.synchronization.priority.remote.RemotePrioritySynchronizer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

// TODO: create provisioner to create new calendar for pomodoro if not exists
@Slf4j
@Component
@AllArgsConstructor
public class WarmUpper {

    private final RemotePrioritySynchronizer synchronizer;
    private final RemotePomodoroDataService remotePomodoroDataService;
    private final PomodoroCalendarEventProcessor pomodoroCalendarEventProcessor;

    @EventListener({ContextRefreshedEvent.class})
    void onStartup() {
        log.info("WarmUpper has started");

        PomodoroMetadataDto remoteData = remotePomodoroDataService.provideRemoteData();

        List<PomodoroDto> remotePomodoro = remoteData.getPomodoros();

        pomodoroCalendarEventProcessor.process(remotePomodoro);

        synchronizer.synchronize(remoteData);

        log.info("WarmUpper successfully finished");
    }

}
