package com.igorgorbunov3333.timer.service.pomodoro.report.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface WorkStandardAmountCalculable {

    PomodoroProperties getPomodoroProperties();

    default int calculateWorkStandardAmount(PeriodDto period, List<LocalDate> dayOffs) {
        LocalDate startDate = period.getStart().toLocalDate();
        LocalDate endDate = period.getEnd().toLocalDate();

        List<LocalDate> standardDays = new ArrayList<>();
        for (LocalDate current = startDate;
             current.isBefore(endDate.plusDays(1L));
             current = current.plusDays(1L)) {
            if (!current.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                    && !current.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                    && !dayOffs.contains(current)) {
                standardDays.add(current);
            }
        }

        int daysAmountToCalculateStandard = CollectionUtils.isEmpty(standardDays) ? 0 : standardDays.size();
        return getPomodoroProperties().getStandard().getWork() * daysAmountToCalculateStandard;
    }

}
