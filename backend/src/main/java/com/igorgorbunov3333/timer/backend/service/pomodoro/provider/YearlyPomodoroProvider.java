package com.igorgorbunov3333.timer.backend.service.pomodoro.provider;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.YearlyPomodoroDto;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.backend.service.pomodoro.period.PomodoroByMonthsDivider;
import com.igorgorbunov3333.timer.backend.service.tag.TagService;
import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class YearlyPomodoroProvider implements BasePomodoroProvider {

    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;
    @Getter
    private final TagService tagService;
    private final PomodoroByMonthsDivider pomodoroByMonthsDivider;
    private final MonthlyPomodoroProvider monthlyPomodoroProvider;

    public YearlyPomodoroDto provideCurrentYearPomodoro(String tag) {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        ZonedDateTime start = today.withDayOfYear(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        List<PomodoroDto> yearlyPomodoro = provide(start, end, tag);

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
