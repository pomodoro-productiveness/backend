package com.igorgorbunov3333.timer.service.pomodoro.calendar;

import com.google.api.services.calendar.model.Event;
import com.igorgorbunov3333.timer.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.google.calendar.CalendarEventProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
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
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class PomodoroCalendarEventProcessor {

    private final GoogleServicesProperties googleServicesProperties;
    private final PomodoroCalendarEventPublisher publisher;
    private final CalendarEventProvider calendarEventProvider;
    private final CurrentTimeService currentTimeService;

    //TODO: Add unit test where latestPomodoroStartTime from the same day and next event should be started later in the same day
    //TODO: Add test - do not process todays pomodoro

    @Async
    @SneakyThrows
    @Transactional
    public void process(List<PomodoroDto> pomodoro) {
        log.info("Started PomodoroCalendarEventProcessor");

        Event latestPomodoroCalendarEvent = calendarEventProvider.provideLatestEvent(googleServicesProperties.getCalendar().getId().getPomodoro());

        ZonedDateTime pomodoroStartTime = LocalDateTime.of(2000, 1, 1, 1, 1, 1).atZone(ZoneId.systemDefault());
        if (latestPomodoroCalendarEvent != null) {
            pomodoroStartTime = Instant.ofEpochMilli(latestPomodoroCalendarEvent.getStart().getDateTime().getValue())
                    .atZone(ZoneId.systemDefault());
        }

        ZonedDateTime latestPomodoroStartTime = pomodoroStartTime;

        log.debug("Last processed pomodoro start time is [{}]", latestPomodoroStartTime);

        ZonedDateTime todayStartDateTime = currentTimeService.getCurrentDateTime()
                .toLocalDate()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault());

        pomodoro.stream()
                .filter(p -> p.getStartTime().isAfter(latestPomodoroStartTime)
                        && p.getStartTime().isBefore(todayStartDateTime))
                .forEach(publisher::publish);
    }

}
