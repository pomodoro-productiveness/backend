package com.igorgorbunov3333.timer.console.service.pomodoro.calendar;

import com.google.api.services.calendar.model.Event;
import com.igorgorbunov3333.timer.console.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.console.service.google.calendar.CalendarEventProvider;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
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
    private final PomodoroComponent pomodoroComponent;

    @Async
    @SneakyThrows
    public void process() {
        log.info("Started PomodoroCalendarEventProcessor");

        Event latestPomodoroCalendarEvent = calendarEventProvider.provideLatestEvent(googleServicesProperties.getCalendar().getId().getPomodoro());

        LocalDate latestPomodoroEventStartDate = LocalDate.of(2000, 1, 1);
        if (latestPomodoroCalendarEvent != null) {
            latestPomodoroEventStartDate = Instant.ofEpochMilli(latestPomodoroCalendarEvent.getStart().getDateTime().getValue())
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate();
        }

        log.debug("Last processed pomodoro start time is [{}]", latestPomodoroEventStartDate);

        LocalDate today = currentTimeService.getCurrentDateTime()
                .toLocalDate();

        pomodoroComponent.getPomodoro(latestPomodoroEventStartDate, today, null)
                .forEach(publisher::publish);
    }

}
