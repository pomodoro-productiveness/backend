package com.igorgorbunov3333.timer.service.pomodoro.work.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.WorkingPomodorosPerformanceRateDto;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class WorkingTimeStandardCalculator {

    public abstract CurrentTimeService getCurrentTimeService();
    public abstract PomodoroProperties getPomodoroProperties();

    public WorkingPomodorosPerformanceRateDto calculate(LocalDate startDate,
                                                        List<LocalDate> dayOffs,
                                                        int workedPomodoro) {
        LocalDate today = getCurrentTimeService().getCurrentDateTime().toLocalDate();

        List<LocalDate> workedDays = new ArrayList<>();
        for (LocalDate current = startDate;
             current.isBefore(today.plusDays(1L));
             current = current.plusDays(1L)) {
            if (!current.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                    && !current.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                    && !dayOffs.contains(current)) {
                workedDays.add(current);
            }
        }

        int workingDaysAmount = CollectionUtils.isEmpty(workedDays) ? 0 : workedDays.size();
        int currentWeekPomodorosAmountStandard = getPomodoroProperties().getAmount().getWork() * workingDaysAmount;

        return new WorkingPomodorosPerformanceRateDto(workedPomodoro - currentWeekPomodorosAmountStandard);
    }

}
