package com.igorgorbunov3333.timer.console.service.warmup;

import com.igorgorbunov3333.timer.console.service.dayoff.DayOffSynchronizer;
import com.igorgorbunov3333.timer.console.service.message.PomodoroStandardReportMessageSender;
import com.igorgorbunov3333.timer.console.service.pomodoro.calendar.PomodoroCalendarEventProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class WarmUpper {

    private final PomodoroCalendarEventProcessor pomodoroCalendarEventProcessor;
    private final DayOffSynchronizer dayOffSynchronizer;
    private final PomodoroStandardReportMessageSender pomodoroStandardReportMessageSender;

    @EventListener({ContextRefreshedEvent.class})
    void onStartup() {
        log.info("WarmUpper has started");

        pomodoroCalendarEventProcessor.process();
        dayOffSynchronizer.synchronize();
        pomodoroStandardReportMessageSender.send();

        log.info("WarmUpper successfully finished");
    }

}
