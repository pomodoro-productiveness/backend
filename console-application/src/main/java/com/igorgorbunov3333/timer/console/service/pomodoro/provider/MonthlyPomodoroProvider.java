package com.igorgorbunov3333.timer.console.service.pomodoro.provider;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MonthlyPomodoroProvider {

    private final CurrentTimeComponent currentTimeComponent;
    private final WeeklyPomodoroProvider weeklyPomodoroProvider;

    public MonthlyPomodoroDto provideCurrentMonthPomodoro(List<PomodoroDto> pomodoro) {
        LocalDateTime today = currentTimeComponent.getCurrentDateTime();

        YearMonth currentMonth = YearMonth.from(today);

        PeriodDto monthPeriod = getMonthPeriod(currentMonth, today.toLocalDate());

        List<PomodoroDto> monthlyPomodoro = pomodoro.stream()
                .filter(p -> !p.getStartTime().toLocalDateTime().isBefore(currentMonth.atDay(1).atStartOfDay())
                        && !p.getEndTime().toLocalDateTime().isAfter(today.toLocalDate().atTime(LocalTime.MAX)))
                .collect(Collectors.toList());

        List<WeeklyPomodoroDto> weeklyPomodoro =
                weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(currentMonth, monthlyPomodoro);

        if (CollectionUtils.isEmpty(weeklyPomodoro)) {
            return MonthlyPomodoroDto.buildEmpty(monthPeriod);
        }

        PeriodDto period = new PeriodDto(
                weeklyPomodoro.get(0).getPeriod().getStart(),
                weeklyPomodoro.get(weeklyPomodoro.size() - 1).getPeriod().getEnd()
        );

        return new MonthlyPomodoroDto(weeklyPomodoro, period);
    }

    public MonthlyPomodoroDto providePomodoroForMonth(YearMonth month, List<PomodoroDto> pomodoro) {
        LocalDate today = currentTimeComponent.getCurrentDateTime().toLocalDate();

        PeriodDto monthPeriod = getMonthPeriod(month, today);

        List<WeeklyPomodoroDto> weeklyPomodoro =
                weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(month, pomodoro);

        if (CollectionUtils.isEmpty(weeklyPomodoro)) {
            return MonthlyPomodoroDto.buildEmpty(monthPeriod);
        }

        LocalDateTime start = weeklyPomodoro.get(0).getPeriod().getStart();
        LocalDateTime end = weeklyPomodoro.get(weeklyPomodoro.size() - 1).getPeriod().getEnd();

        PeriodDto period = new PeriodDto(start, end);

        return new MonthlyPomodoroDto(weeklyPomodoro, period);
    }

    private PeriodDto getMonthPeriod(YearMonth month, LocalDate today) {
        LocalDate periodEnd;
        if (YearMonth.from(today).isAfter(month)) {
            periodEnd = month.atEndOfMonth();
        } else {
            periodEnd = today;
        }

        return new PeriodDto(
                month.atDay(1).atStartOfDay(),
                periodEnd.atTime(LocalTime.MAX)
        );
    }

}
