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

import java.time.LocalDate;
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

    public MonthlyPomodoroDto provideMonthlyPomodoro(PeriodDto period, List<PomodoroDto> pomodoro) {
        List<WeeklyPomodoroDto> weeklyPomodoro =
                weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(period, pomodoro);

        return new MonthlyPomodoroDto(weeklyPomodoro, period);
    }

    public MonthlyPomodoroDto provideCurrentMonthlyPomodoro(List<PomodoroDto> pomodoro) {
        PeriodDto currentMonthPeriod = getCurrentMonthPeriod();

        List<PomodoroDto> monthlyPomodoro = pomodoro.stream()
                .filter(p -> !p.getStartTime().toLocalDateTime().isBefore(currentMonthPeriod.getStart())
                        && !p.getEndTime().toLocalDateTime().isAfter(currentMonthPeriod.getEnd()))
                .collect(Collectors.toList());

        List<WeeklyPomodoroDto> weeklyPomodoro =
                weeklyPomodoroProvider.provideWeeklyPomodoroForPeriod(currentMonthPeriod, monthlyPomodoro);

        return new MonthlyPomodoroDto(weeklyPomodoro, currentMonthPeriod);
    }

    private PeriodDto getCurrentMonthPeriod() {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate(); //TODO: extract common code

        YearMonth yearMonth = YearMonth.from(today); //TODO: extract common code
        LocalDate startDayOfMonth = yearMonth.atDay(1); //TODO: extract common code

        return new PeriodDto(startDayOfMonth.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

}
