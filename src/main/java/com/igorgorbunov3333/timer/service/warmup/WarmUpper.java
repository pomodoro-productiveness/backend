package com.igorgorbunov3333.timer.service.warmup;

import com.igorgorbunov3333.timer.service.pomodoro.calendar.PomodoroCalendarEventProcessor;
import com.igorgorbunov3333.timer.service.tag.bunch.PomodoroTagBunchCleaner;
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
    private final PomodoroTagBunchCleaner pomodoroTagBunchCleaner;

    @EventListener({ContextRefreshedEvent.class})
    void onStartup() {
        log.info("WarmUpper has started");

        pomodoroCalendarEventProcessor.process();
        pomodoroTagBunchCleaner.clean();

        log.info("WarmUpper successfully finished");
    }

}
