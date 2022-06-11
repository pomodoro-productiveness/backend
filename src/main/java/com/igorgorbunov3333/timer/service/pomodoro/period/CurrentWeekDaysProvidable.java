package com.igorgorbunov3333.timer.service.pomodoro.period;

import com.igorgorbunov3333.timer.service.util.CurrentTimeService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface CurrentWeekDaysProvidable {

    CurrentTimeService getCurrentTimeService();

    default List<DayOfWeek> provideDaysOfCurrentWeek() {
        LocalDate currentDay = getCurrentTimeService().getCurrentDateTime().toLocalDate();
        int currentDayOfWeek = currentDay.getDayOfWeek().getValue();

        return IntStream.range(DayOfWeek.MONDAY.getValue(), currentDayOfWeek + 1)
                .boxed()
                .map(DayOfWeek::of)
                .collect(Collectors.toList());
    }

}
