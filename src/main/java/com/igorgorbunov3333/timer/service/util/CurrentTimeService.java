package com.igorgorbunov3333.timer.service.util;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class CurrentTimeService {

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
