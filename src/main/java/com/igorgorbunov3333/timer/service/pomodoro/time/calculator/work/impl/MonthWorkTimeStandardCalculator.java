package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.service.pomodoro.period.MonthStartDayProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.MonthlyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.WorkTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.CalculationPeriod;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MonthWorkTimeStandardCalculator implements WorkTimeStandardCalculator, MonthStartDayProvidable {

    @Getter
    private final CurrentTimeService currentTimeService;
    private final DayOffRepository dayOffRepository;
    @Getter
    private final PomodoroProperties pomodoroProperties;
    private final MonthlyLocalPomodoroProvider monthlyLocalPomodoroProvider;

    @Override
    public int calculate() {
        LocalDate startDayOfMonth = provideStartDayOfMonth();

        List<LocalDate> dayOffs = dayOffRepository.findByDayGreaterThanEqualOrderByDay(startDayOfMonth).stream()
                .map(DayOff::getDay)
                .collect(Collectors.toList());

        int workedPomodoroAmount = monthlyLocalPomodoroProvider.provide(pomodoroProperties.getTag().getWork())
                .size();

        return calculate(startDayOfMonth, dayOffs, workedPomodoroAmount);
    }

    @Override
    public CalculationPeriod period() {
        return CalculationPeriod.MONTH;
    }

}
