package com.igorgorbunov3333.timer.service.pomodoro.period;

import com.igorgorbunov3333.timer.service.util.CurrentTimeService;

import java.time.DayOfWeek;
import java.time.LocalDate;

public interface WeekStartDayProvidable {

    CurrentTimeService getCurrentTimeService();

    default LocalDate provideStartDayOfWeek() {
        LocalDate currentDay = getCurrentTimeService().getCurrentDateTime().toLocalDate();
        return currentDay.with(DayOfWeek.MONDAY);
    }

}
