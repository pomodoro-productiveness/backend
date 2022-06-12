package com.igorgorbunov3333.timer.service.pomodoro.period;

import com.igorgorbunov3333.timer.service.util.CurrentTimeService;

import java.time.LocalDate;
import java.time.YearMonth;

public interface MonthStartDayProvidable {

    CurrentTimeService getCurrentTimeService();

    default LocalDate provideStartDayOfMonth() {
        LocalDate today = getCurrentTimeService().getCurrentDateTime().toLocalDate();
        YearMonth yearMonth = YearMonth.from(today);
        return yearMonth.atDay(1);
    }

}
