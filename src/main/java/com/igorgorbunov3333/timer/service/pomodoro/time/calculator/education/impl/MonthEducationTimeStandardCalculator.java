package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.service.pomodoro.period.MonthStartDayProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.MonthlyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.education.EducationTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.CalculationPeriod;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class MonthEducationTimeStandardCalculator implements EducationTimeStandardCalculator, MonthStartDayProvidable {

    @Getter
    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroProperties pomodoroProperties;
    private final MonthlyLocalPomodoroProvider monthlyLocalPomodoroProvider;

    @Override
    public int calculate() {
        int workedPomodoroAmount = monthlyLocalPomodoroProvider.provide(pomodoroProperties.getTag().getEducation())
                .size();

        LocalDate startDayOfMonth = provideStartDayOfMonth();

        return calculate(startDayOfMonth, workedPomodoroAmount);
    }

    @Override
    public CalculationPeriod period() {
        return CalculationPeriod.MONTH;
    }

}
