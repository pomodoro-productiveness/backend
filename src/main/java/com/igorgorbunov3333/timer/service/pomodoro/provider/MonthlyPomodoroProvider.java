package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.MonthlyPomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.period.WeeklyPomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.tag.TagService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MonthlyPomodoroProvider implements BasePomodoroProvider {

    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;
    @Getter
    private final TagService tagService;
    private final WeeklyPomodoroProvider weeklyPomodoroProvider;

    public MonthlyPomodoroDto providePomodoroForMonth(YearMonth month, List<PomodoroDto> pomodoro) {
        List<WeeklyPomodoroDto> weeklyPomodoro =
                weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(month, pomodoro);

        if (CollectionUtils.isEmpty(weeklyPomodoro)) {
            return MonthlyPomodoroDto.buildEmpty();
        }

        LocalDateTime start = weeklyPomodoro.get(0).getPeriod().getStart();
        LocalDateTime end = weeklyPomodoro.get(weeklyPomodoro.size() - 1).getPeriod().getEnd();

        PeriodDto period = new PeriodDto(start, end);

        return new MonthlyPomodoroDto(weeklyPomodoro, period);
    }

    public MonthlyPomodoroDto provideCurrentMonthPomodoro(List<PomodoroDto> pomodoro) {
        LocalDateTime today = currentTimeService.getCurrentDateTime();

        YearMonth currentMonth = YearMonth.from(today);

        List<PomodoroDto> monthlyPomodoro = pomodoro.stream()
                .filter(p -> !p.getStartTime().toLocalDateTime().isBefore(currentMonth.atDay(1).atStartOfDay())
                        && !p.getEndTime().toLocalDateTime().isAfter(today.toLocalDate().atTime(LocalTime.MAX)))
                .collect(Collectors.toList());

        List<WeeklyPomodoroDto> weeklyPomodoro =
                weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(currentMonth, monthlyPomodoro);

        if (CollectionUtils.isEmpty(weeklyPomodoro)) {
            return MonthlyPomodoroDto.buildEmpty();
        }

        PeriodDto period = new PeriodDto(
                weeklyPomodoro.get(0).getPeriod().getStart(),
                weeklyPomodoro.get(weeklyPomodoro.size() - 1).getPeriod().getEnd()
        );

        return new MonthlyPomodoroDto(weeklyPomodoro, period);
    }

}
