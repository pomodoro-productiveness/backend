package com.igorgorbunov3333.timer.service.pomodoro.work.calculator.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.WorkingPomodorosPerformanceRateDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.repository.DayOffRepository;
import com.igorgorbunov3333.timer.service.pomodoro.period.CurrentWeekDaysProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeeklyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.work.calculator.WorkTimeStandardCalculator;
import com.igorgorbunov3333.timer.service.pomodoro.work.calculator.enums.CalculationPeriod;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WeeklyWorkTimeStandardCalculator implements CurrentWeekDaysProvidable, WorkTimeStandardCalculator {

    private final WeeklyLocalPomodoroProvider weeklyLocalPomodoroProvider;

    @Getter
    private final PomodoroProperties pomodoroProperties;
    @Getter
    private final CurrentTimeService currentTimeService;
    private final DayOffRepository dayOffRepository;

    @Override
    public WorkingPomodorosPerformanceRateDto calculate() {
        LocalDate currentDay = currentTimeService.getCurrentDateTime().toLocalDate();
        LocalDate startOfWeek = currentDay.with(DayOfWeek.MONDAY);

        List<LocalDate> currentWeekDayOffList = dayOffRepository.findByDayGreaterThanEqualOrderByDay(startOfWeek).stream()
                .map(DayOff::getDay)
                .collect(Collectors.toList());

        int weeklyWorkedPomodoros = getWeeklyWorkedPomodorosAmount();

        return calculate(startOfWeek, currentWeekDayOffList, weeklyWorkedPomodoros);
    }

    private int getWeeklyWorkedPomodorosAmount() {
        List<PomodoroDto> weeklyPomodoros = weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(pomodoroProperties.getTag().getWork());

        if (CollectionUtils.isEmpty(weeklyPomodoros)) {
            return 0;
        }

        return weeklyPomodoros.size();
    }

    @Override
    public CalculationPeriod period() {
        return CalculationPeriod.WEEK;
    }

}
