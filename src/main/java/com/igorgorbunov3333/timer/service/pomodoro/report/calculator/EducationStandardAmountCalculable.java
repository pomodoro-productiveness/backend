package com.igorgorbunov3333.timer.service.pomodoro.report.calculator;

import com.igorgorbunov3333.timer.config.properties.PomodoroProperties;
import com.igorgorbunov3333.timer.model.dto.PeriodDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface EducationStandardAmountCalculable {

    PomodoroProperties getPomodoroProperties();

    default int calculateEducationStandardAmount(PeriodDto period) {
        LocalDate startDate = period.getStart().toLocalDate();
        LocalDate endDate = period.getEnd().toLocalDate();

        List<LocalDate> days = new ArrayList<>();
        for (LocalDate current = startDate;
             current.isBefore(endDate.plusDays(1L));
             current = current.plusDays(1L)) {
            if (!current.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                    && !current.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                days.add(current);
            }
        }

        return getPomodoroProperties().getStandard().getEducation() * days.size();
    }

}
