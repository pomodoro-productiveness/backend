package com.igorgorbunov3333.timer.console.service.google.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.igorgorbunov3333.timer.console.service.google.RateLimitedGoogleApiExecutor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Slf4j
@Component
@AllArgsConstructor
public class GoogleCalendarEventPublisher {

    private final Calendar service;
    private final RateLimitedGoogleApiExecutor rateLimitedGoogleApiExecutor;

    @SneakyThrows
    public void publishEvent(String summary, String calendarId, String colorId, long startTime, long endTime) { //TODO: refactor
        Event event = new Event()
                .setSummary(summary)
                .setColorId(colorId);

        DateTime startDateTime = new DateTime(startTime);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(ZoneId.systemDefault().getId());
        event.setStart(start);

        DateTime endDateTime = new DateTime(endTime);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(ZoneId.systemDefault().getId());
        event.setEnd(end);

        Calendar.Events.Insert insert = service.events().insert(calendarId, event);

        rateLimitedGoogleApiExecutor.execute(insert);

        log.debug("Calendar event with summary [{}], startTime [{}] and endTime [{}] successfully published", summary, start.getDateTime(), end.getDateTime());
    }

}
