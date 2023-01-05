package com.igorgorbunov3333.timer.console.service.pomodoro.period;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class PomodoroByWeekDivider {

    private final CurrentTimeComponent currentTimeComponent;

    public List<PeriodDto> dividePeriodByWeeks(PeriodDto period) {
        LocalDate today = currentTimeComponent.getCurrentDateTime().toLocalDate();

        return divideForWeeklyPeriods(period, today);
    }

    private List<PeriodDto> divideForWeeklyPeriods(PeriodDto monthPeriod, LocalDate today) {
        LocalDate startDate = monthPeriod.getStart().toLocalDate();
        LocalDate endDate = monthPeriod.getEnd().toLocalDate();

        List<PeriodDto> periods = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            PeriodDto period = getWeekByDate(current);

            LocalDateTime startPeriod = period.getStart();
            LocalDateTime endPeriod = period.getEnd();

            if (startPeriod.isBefore(monthPeriod.getStart())) {
                startPeriod = monthPeriod.getStart();
            }

            if (endPeriod.toLocalDate().isAfter(today)) {
                endPeriod = today.atTime(LocalTime.MAX);
            } else if (endPeriod.toLocalDate().isAfter(endDate)) {
                endPeriod = endDate.atTime(LocalTime.MAX);
            }

            period = new PeriodDto(startPeriod, endPeriod);

            periods.add(period);

            current = period.getEnd().toLocalDate().plusDays(1L);
        }

        return periods;
    }

    private PeriodDto getWeekByDate(LocalDate date) {
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);

        LocalDateTime periodStart = startOfWeek.atStartOfDay();
        LocalDateTime periodEnd = startOfWeek.plusDays(DayOfWeek.SATURDAY.getValue()).atTime(LocalTime.MAX);

        return new PeriodDto(periodStart, periodEnd);
    }

}
