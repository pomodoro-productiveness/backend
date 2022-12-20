package com.igorgorbunov3333.timer.console.service.dayoff;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.igorgorbunov3333.timer.console.config.properties.GoogleServicesProperties;
import com.igorgorbunov3333.timer.console.rest.dto.DayOffDto;
import com.igorgorbunov3333.timer.console.service.google.calendar.CalendarEventProvider;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RemoteDayOffProvider {

    private static final String DAY_OFF_SUMMARY = "DAY_OFF";

    private final CalendarEventProvider calendarEventProvider;
    private final GoogleServicesProperties googleServicesProperties;

    @SneakyThrows
    public List<DayOffDto> provide() {
        List<Event> dayOffEvents = calendarEventProvider.provideAllEvents(googleServicesProperties.getCalendar().getId().getDayOff());

        return dayOffEvents.stream()
                .filter(event -> DAY_OFF_SUMMARY.equals(event.getSummary()))
                .map(this::mapCalendarEventToDayOff)
                .collect(Collectors.toList());
    }

    private DayOffDto mapCalendarEventToDayOff(Event event) {
        EventDateTime dateTime = event.getStart();
        LocalDate date = Instant.ofEpochMilli(dateTime.getDateTime().getValue())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return new DayOffDto(date);
    }

}
