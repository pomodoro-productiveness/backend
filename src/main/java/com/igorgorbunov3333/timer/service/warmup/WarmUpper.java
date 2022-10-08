package com.igorgorbunov3333.timer.service.warmup;

import com.igorgorbunov3333.timer.service.dayoff.DayOffSynchronizer;
import com.igorgorbunov3333.timer.service.message.SendMessageStarter;
import com.igorgorbunov3333.timer.service.pomodoro.calendar.PomodoroCalendarEventProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class WarmUpper {
//TODO: pass local pomodoros for PomodoroCalendarEventProcessor to process

    private final PomodoroCalendarEventProcessor pomodoroCalendarEventProcessor;
    private final DayOffSynchronizer dayOffSynchronizer;
    private final SendMessageStarter sendMessageStarter;

    @EventListener({ContextRefreshedEvent.class})
    void onStartup() {
        log.info("WarmUpper has started");

        pomodoroCalendarEventProcessor.process();
        dayOffSynchronizer.synchronize();
        sendMessageStarter.start();

        log.info("WarmUpper successfully finished");
    }

}
