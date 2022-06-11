package com.igorgorbunov3333.timer.service.dayoff;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.igorgorbunov3333.timer.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RemoteDayOffProvider {

    private static final String DAY_OFF_SUMMARY = "DAY_OFF";

    private final Calendar service;
    private final GoogleServicesProperties googleServicesProperties;

    @SneakyThrows
    public List<DayOff> provide() {
        String pageToken = null;
        List<Event> dayOffEvents = new ArrayList<>();
        do {
            Events pageTokenEvents = getPageTokenEvents(pageToken);
            List<Event> pageTokenDayOffEvents = getPageTokenDayOffEvents(pageTokenEvents);
            dayOffEvents.addAll(pageTokenDayOffEvents);
            pageToken = pageTokenEvents.getNextPageToken();
        } while (pageToken != null);

        return dayOffEvents.stream()
                .map(this::mapCalendarEventToDayOff)
                .collect(Collectors.toList());
    }

    private Events getPageTokenEvents(String pageToken) throws IOException {
        return service.events()
                .list(googleServicesProperties.getCalendarId())
                .setPageToken(pageToken)
                .execute();
    }

    private List<Event> getPageTokenDayOffEvents(Events pageTokenEvents) {
        return pageTokenEvents.getItems().stream()
                .filter(event -> StringUtils.isNotBlank(event.getSummary()))
                .filter(event -> event.getSummary().equals(DAY_OFF_SUMMARY))
                .collect(Collectors.toList());
    }

    private DayOff mapCalendarEventToDayOff(Event event) {
        EventDateTime dateTime = event.getStart();
        LocalDate date = Instant.ofEpochMilli(dateTime.getDateTime().getValue())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return new DayOff(null, date);
    }

}
