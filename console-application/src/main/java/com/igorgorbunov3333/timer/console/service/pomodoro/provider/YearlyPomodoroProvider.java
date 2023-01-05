package com.igorgorbunov3333.timer.console.service.pomodoro.provider;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.console.rest.dto.pomodoro.period.YearlyPomodoroDto;
import com.igorgorbunov3333.timer.console.service.pomodoro.PomodoroComponent;
import com.igorgorbunov3333.timer.console.service.pomodoro.period.PomodoroByMonthsDivider;
import com.igorgorbunov3333.timer.console.service.util.CurrentTimeComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class YearlyPomodoroProvider {

    private final CurrentTimeComponent currentTimeComponent;
    private final PomodoroComponent pomodoroComponent;
    private final PomodoroByMonthsDivider pomodoroByMonthsDivider;
    private final MonthlyPomodoroProvider monthlyPomodoroProvider;

    public YearlyPomodoroDto provideCurrentYearPomodoro(String tag) {
        LocalDate today = currentTimeComponent.getCurrentDateTime().toLocalDate();
        LocalDate start = today.withDayOfYear(1);

        List<PomodoroDto> yearlyPomodoro = pomodoroComponent.getPomodoro(start, today, tag);

        Map<YearMonth, List<PomodoroDto>> monthsToPomodoro = pomodoroByMonthsDivider.divide(yearlyPomodoro);

        LocalDateTime periodStart = monthsToPomodoro.entrySet()
                .iterator()
                .next()
                .getKey()
                .atDay(1)
                .atStartOfDay();

        LocalDateTime periodEnd = getPeriodEnd(monthsToPomodoro, today);

        List<MonthlyPomodoroDto> monthlyPomodoro = new ArrayList<>();
        for (Map.Entry<YearMonth, List<PomodoroDto>> entry : monthsToPomodoro.entrySet()) {
            MonthlyPomodoroDto monthlyPomodoroDto = monthlyPomodoroProvider.providePomodoroForMonth(entry.getKey(), entry.getValue());

            monthlyPomodoro.add(monthlyPomodoroDto);
        }

        PeriodDto period = new PeriodDto(periodStart, periodEnd);

        return new YearlyPomodoroDto(monthlyPomodoro, period);
    }

    private LocalDateTime getPeriodEnd(Map<YearMonth, List<PomodoroDto>> monthsToPomodoro, LocalDate today) {
        if (monthsToPomodoro.isEmpty()) {
            return today.atTime(LocalTime.MAX);
        }

        Iterator<Map.Entry<YearMonth, List<PomodoroDto>>> yearMonthIterator = monthsToPomodoro.entrySet().iterator();

        YearMonth yearMonth = yearMonthIterator.next().getKey();

        while (yearMonthIterator.hasNext()) {
            yearMonth = yearMonthIterator.next().getKey();
        }

        if (yearMonth.isBefore(YearMonth.from(today))) {
            return yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        }

        return today.atTime(LocalTime.MAX);
    }

}
