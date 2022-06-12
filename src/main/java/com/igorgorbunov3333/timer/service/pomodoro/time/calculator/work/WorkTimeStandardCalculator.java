package com.igorgorbunov3333.timer.service.pomodoro.time.calculator.work;

import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.BaseTimeStandardCalculator;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface WorkTimeStandardCalculator extends BaseTimeStandardCalculator {

    default int calculate(LocalDate startDate, List<LocalDate> dayOffs, int actualAmount) {
        LocalDate today = getCurrentTimeService().getCurrentDateTime().toLocalDate();

        List<LocalDate> days = new ArrayList<>();
        for (LocalDate current = startDate;
             current.isBefore(today.plusDays(1L));
             current = current.plusDays(1L)) {
            if (!current.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                    && !current.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                    && !dayOffs.contains(current)) {
                days.add(current);
            }
        }

        int daysAmount = CollectionUtils.isEmpty(days) ? 0 : days.size();
        int standardAmount = getPomodoroProperties().getStandard().getWork() * daysAmount;

        return actualAmount - standardAmount;
    }

}
