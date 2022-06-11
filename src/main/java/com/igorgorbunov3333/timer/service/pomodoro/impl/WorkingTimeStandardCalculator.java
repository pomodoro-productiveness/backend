package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.WorkingPomodorosPerformanceRateDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.dayoff.LocalDayOffProvider;
import com.igorgorbunov3333.timer.service.pomodoro.period.CurrentWeekDaysProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.WeeklyLocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WorkingTimeStandardCalculator implements CurrentWeekDaysProvidable {

    private final WeeklyLocalPomodoroProvider weeklyLocalPomodoroProvider;
    private final PomodoroProperties pomodoroProperties;
    @Getter
    private final CurrentTimeService currentTimeService;
    private final LocalDayOffProvider localDayOffProvider;

    public WorkingPomodorosPerformanceRateDto calculate() {
        List<DayOfWeek> currentWeekDayOffList = localDayOffProvider.provideCurrentWeekDayOffs().stream()
                .map(dayOff -> dayOff.getDay().getDayOfWeek())
                .collect(Collectors.toList());

        List<DayOfWeek> workingDays = provideDaysOfCurrentWeek().stream()
                .filter(dayOff -> !currentWeekDayOffList.contains(dayOff))
                .collect(Collectors.toList());

        workingDays.remove(DayOfWeek.SATURDAY);
        workingDays.remove(DayOfWeek.SUNDAY);

        int workingDaysAmount = CollectionUtils.isEmpty(workingDays) ? 0 : workingDays.size();
        int currentWeekPomodorosAmountStandard = pomodoroProperties.getAmount().getWork() * workingDaysAmount;

        int weeklyWorkedPomodoros = getWeeklyWorkedPomodorosAmount(currentWeekPomodorosAmountStandard);

        return new WorkingPomodorosPerformanceRateDto(weeklyWorkedPomodoros - currentWeekPomodorosAmountStandard);
    }

    private int getWeeklyWorkedPomodorosAmount(int currentWeekPomodorosAmountStandard) {
        List<PomodoroDto> weeklyPomodoros = weeklyLocalPomodoroProvider.provideCurrentWeekPomodoros(pomodoroProperties.getTag().getWork());

        if (CollectionUtils.isEmpty(weeklyPomodoros)) {
            return 0;
        }

        return weeklyPomodoros.size();
    }

}
