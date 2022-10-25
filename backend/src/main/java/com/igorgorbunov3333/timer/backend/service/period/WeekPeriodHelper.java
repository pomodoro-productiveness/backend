package com.igorgorbunov3333.timer.backend.service.period;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class WeekPeriodHelper {

    @Getter
    private final CurrentTimeService currentTimeService;

    public PeriodDto providePreviousWeekPeriod() {
        LocalDate currentDate = currentTimeService.getCurrentDateTime().toLocalDate();

        DayOfWeek currentDateDayOfWeek = DayOfWeek.from(currentDate);
        LocalDate previousWeekSunday = currentDate.minusDays(currentDateDayOfWeek.getValue());

        return getWeekByDate(previousWeekSunday);
    }

    public List<PeriodDto> dividePeriodByWeeks(PeriodDto period) {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

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
