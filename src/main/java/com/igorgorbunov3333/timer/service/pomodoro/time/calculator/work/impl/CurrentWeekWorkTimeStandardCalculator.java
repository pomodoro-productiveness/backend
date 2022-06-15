package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.service.pomodoro.period.CurrentWeekDaysProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.period.WeekStartDayProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.LocalPomodoroProviderCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Getter
@Service
@AllArgsConstructor
public class CurrentWeekWorkTimeStandardCalculator implements CurrentWeekDaysProvidable, WeekStartDayProvidable, WorkTimeStandardCalculator {

    private final PomodoroProperties pomodoroProperties;
    private final CurrentTimeService currentTimeService;
    private final DayOffRepository dayOffRepository;
    private final LocalPomodoroProviderCoordinator localPomodoroProviderCoordinator;

    @Override
    public int calculate() {
        LocalDate startOfWeek = provideStartDayOfWeek();

        return calculate(PomodoroPeriod.WEEK, startOfWeek);
    }

    @Override
    public PomodoroPeriod period() {
        return PomodoroPeriod.WEEK;
    }

}
