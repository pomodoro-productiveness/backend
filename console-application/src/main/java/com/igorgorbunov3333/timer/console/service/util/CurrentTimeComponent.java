package com.igorgorbunov3333.timer.console.service.util;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class CurrentTimeComponent {

    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    public Pair<ZonedDateTime, ZonedDateTime> getCurrentDayPeriod() {
        LocalDateTime currentTime = getCurrentDateTime();
        ZonedDateTime start = currentTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault());
        ZonedDateTime end = currentTime.toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        return Pair.of(start, end);
    }

}
