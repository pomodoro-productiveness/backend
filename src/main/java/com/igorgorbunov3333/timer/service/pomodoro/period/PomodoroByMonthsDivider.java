package com.igorgorbunov3333.timer.service.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroByMonthsDivider {

    private final CurrentTimeService currentTimeService;

    public Map<PeriodDto, List<PomodoroDto>> divide(List<PomodoroDto> pomodoro) {
        LocalDateTime localTime = currentTimeService.getCurrentDateTime();
        YearMonth currentYearMonth = YearMonth.from(localTime);

        LocalDate yearStart = LocalDate.of(currentYearMonth.getYear(), 1, 1);
        LocalDate today = localTime.toLocalDate();

        List<PeriodDto> monthlyPeriods = new ArrayList<>();

        LocalDate current = yearStart;
        while (!current.isAfter(today)) {
            PeriodDto monthPeriod = getCurrentMonthByDate(current);

            if (current.isAfter(today)) {
                monthPeriod = new PeriodDto(monthPeriod.getStart(), today.atTime(LocalTime.MAX));
            }

            monthlyPeriods.add(monthPeriod);

            current = monthPeriod.getEnd().toLocalDate().plusDays(1L);
        }

        Map<PeriodDto, List<PomodoroDto>> periodsByPomodoro = new LinkedHashMap<>();
        for (PeriodDto period : monthlyPeriods) {
            List<PomodoroDto> monthlyPomodoro = pomodoro.stream()
                    .filter(p -> !p.getStartTime().toLocalDateTime().isBefore(period.getStart())
                            && !p.getStartTime().toLocalDateTime().isAfter(period.getEnd()))
                    .collect(Collectors.toList());

            periodsByPomodoro.put(period, monthlyPomodoro);
        }

        return periodsByPomodoro;
    }

    private PeriodDto getCurrentMonthByDate(LocalDate date) {
        YearMonth currentMonth = YearMonth.from(date);

        LocalDateTime startPeriod = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endPeriod = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return new PeriodDto(startPeriod, endPeriod);
    }

}
