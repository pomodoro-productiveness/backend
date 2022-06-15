package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.service.pomodoro.period.MonthStartDayProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.LocalPomodoroProviderCoordinator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Getter
@Service
@AllArgsConstructor
public class MonthEducationTimeStandardCalculator implements EducationTimeStandardCalculator, MonthStartDayProvidable {

    private final CurrentTimeService currentTimeService;
    private final PomodoroProperties pomodoroProperties;
    private final LocalPomodoroProviderCoordinator localPomodoroProviderCoordinator;

    @Override
    public int calculate() {
        LocalDate startDayOfMonth = provideStartDayOfMonth();

        return calculate(PomodoroPeriod.MONTH, startDayOfMonth);
    }

    @Override
    public PomodoroPeriod period() {
        return PomodoroPeriod.MONTH;
    }

}
