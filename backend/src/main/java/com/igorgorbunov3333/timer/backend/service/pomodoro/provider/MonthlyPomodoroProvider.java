package com.igorgorbunov3333.timer.backend.service.pomodoro.provider;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@AllArgsConstructor
public class MonthlyPomodoroProvider implements BasePomodoroProvider {

    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;
    private final WeeklyPomodoroProvider weeklyPomodoroProvider;

    public MonthlyPomodoroDto providePomodoroForMonth(YearMonth month, List<PomodoroDto> pomodoro) {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        PeriodDto monthPeriod = getMonthPeriod(month, today);

        List<WeeklyPomodoroDto> weeklyPomodoro =
                weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(pomodoro, monthPeriod);

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
