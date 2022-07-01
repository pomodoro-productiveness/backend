package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.service.pomodoro.provider.PomodoroProviderCoordinator;
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
public class CurrentDayWorkTimeStandardCalculator implements WorkTimeStandardCalculator {

    private final CurrentTimeService currentTimeService;
    private final PomodoroProperties pomodoroProperties;
    private final DayOffRepository dayOffRepository;
    private final PomodoroProviderCoordinator pomodoroProviderCoordinator;

    @Override
    public int calculate() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        return calculate(PomodoroPeriod.DAY, today);
    }

    @Override
    public PomodoroPeriod period() {
        return PomodoroPeriod.DAY;
    }

}
