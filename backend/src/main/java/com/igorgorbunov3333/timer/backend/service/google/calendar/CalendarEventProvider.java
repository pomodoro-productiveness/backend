package com.igorgorbunov3333.timer.backend.service.google.calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.igorgorbunov3333.timer.backend.service.google.RateLimitedGoogleApiExecutor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class CalendarEventProvider {

    private final Calendar service;
    private final RateLimitedGoogleApiExecutor rateLimitedGoogleApiExecutor;

    public Event provideLatestEvent(String calendarId) {
        try {
            return provideLatest(calendarId);
        } catch (Exception e) {
            log.error("Error while providing events for google calendar with id " + calendarId, e);
            return null;
        }
    }

    @SneakyThrows
    private Event provideLatest(String calendarId) {
        String pageToken = null;
        Event latestEvent = null;
        do {
            Events pageTokenEvents = getPageTokenEvents(pageToken, calendarId);
            List<Event> events = pageTokenEvents.getItems();
            if (!events.isEmpty()) {
                latestEvent = events.get(events.size() - 1);
            }
            pageToken = pageTokenEvents.getNextPageToken();
        } while (pageToken != null);

        return latestEvent;
    }

    public List<Event> provideAllEvents(String calendarId) {
        try {
            return provide(calendarId);
        } catch (Exception e) {
            log.error("Error while providing events for google calendar with id " + calendarId, e);
            return List.of();
        }
    }

    @SneakyThrows
    private List<Event> provide(String calendarId) {
        String pageToken = null;
        List<Event> events = new ArrayList<>();

        do {
            Events pageTokenEvents = getPageTokenEvents(pageToken, calendarId);
            events.addAll(pageTokenEvents.getItems());
            pageToken = pageTokenEvents.getNextPageToken();
        } while (pageToken != null);

        return events;
    }

    private Events getPageTokenEvents(String pageToken, String calendarId) throws IOException {
        Calendar.Events.List list = service.events()
                .list(calendarId)
                .setPageToken(pageToken);

        return rateLimitedGoogleApiExecutor.execute(list);
    }

}
