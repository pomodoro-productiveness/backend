package com.igorgorbunov3333.timer.backend.service.pomodoro.calendar;

import com.google.api.services.calendar.model.Event;
import com.igorgorbunov3333.timer.backend.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.backend.service.google.calendar.CalendarEventProvider;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl.DefaultPomodoroProvider;
import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class PomodoroCalendarEventProcessor {

    private final GoogleServicesProperties googleServicesProperties;
    private final PomodoroCalendarEventPublisher publisher;
    private final CalendarEventProvider calendarEventProvider;
    private final CurrentTimeService currentTimeService;
    private final DefaultPomodoroProvider pomodoroProvider;

    //TODO: Add unit test where latestPomodoroStartTime from the same day and next event should be started later in the same day
    //TODO: Add test - do not process todays pomodoro

    @Async
    @SneakyThrows
    @Transactional
    public void process() {
        log.info("Started PomodoroCalendarEventProcessor");

        Event latestPomodoroCalendarEvent = calendarEventProvider.provideLatestEvent(googleServicesProperties.getCalendar().getId().getPomodoro());

        ZonedDateTime latestPomodoroEventStartTime = LocalDateTime.of(2000, 1, 1, 1, 1, 1).atZone(ZoneId.systemDefault());
        if (latestPomodoroCalendarEvent != null) {
            latestPomodoroEventStartTime = Instant.ofEpochMilli(latestPomodoroCalendarEvent.getStart().getDateTime().getValue())
                    .atZone(ZoneId.systemDefault());
        }

        log.debug("Last processed pomodoro start time is [{}]", latestPomodoroEventStartTime);

        ZonedDateTime todayStartDateTime = currentTimeService.getCurrentDateTime()
                .toLocalDate()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault());

        pomodoroProvider.provide(latestPomodoroEventStartTime, todayStartDateTime, null)
                .forEach(publisher::publish);
    }

}
