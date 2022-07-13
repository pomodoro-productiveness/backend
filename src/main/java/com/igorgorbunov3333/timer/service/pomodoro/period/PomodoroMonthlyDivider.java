package com.igorgorbunov3333.timer.service.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroMonthlyDivider {

    public Map<PeriodDto, List<PomodoroDto>> divide(List<PomodoroDto> pomodoro) {
        if (CollectionUtils.isEmpty(pomodoro)) {
            return Map.of();
        }

        Map<PeriodDto, List<PomodoroDto>> periods = new LinkedHashMap<>();

        PomodoroDto currentPomodoro = pomodoro.get(0);
        PomodoroDto lastPomodoro = pomodoro.get(pomodoro.size() - 1);

        while (true) {
            PeriodDto period = buildPeriod(currentPomodoro, lastPomodoro);
            List<PomodoroDto> monthlyPomodoro = filterPomodoro(pomodoro, period);

            periods.put(period, monthlyPomodoro);

            currentPomodoro = monthlyPomodoro.get(monthlyPomodoro.size() - 1);
            if (currentPomodoro.equals(lastPomodoro)) {
                break;
            } else {
                currentPomodoro = pomodoro.get(pomodoro.indexOf(currentPomodoro) + 1);
            }
        }

        return periods;
    }

    private PeriodDto buildPeriod(PomodoroDto pomodoro, PomodoroDto lastPomodoro) {
        LocalDate endOfMonth = getEndOfMonth(pomodoro);

        LocalDateTime endPeriod = endOfMonth.isBefore(lastPomodoro.getStartTime().toLocalDate())
                ? endOfMonth.atTime(LocalTime.MAX)
                : lastPomodoro.getStartTime().toLocalDate().atTime(LocalTime.MAX);

        return new PeriodDto(
                pomodoro.getStartTime()
                        .toLocalDateTime()
                        .toLocalDate()
                        .atStartOfDay(),
                endPeriod
        );
    }

    private LocalDate getEndOfMonth(PomodoroDto pomodoro) {
        LocalDate date = pomodoro.getStartTime().toLocalDate();
        return YearMonth.from(date).atEndOfMonth();
    }

    private List<PomodoroDto> filterPomodoro(List<PomodoroDto> pomodoro, PeriodDto period) {
        return pomodoro.stream()
                .filter(p -> p.getStartTime().toLocalDateTime().isAfter(period.getStart())
                        && p.getStartTime().toLocalDateTime().isBefore(period.getEnd()))
                .collect(Collectors.toList());
    }

}
