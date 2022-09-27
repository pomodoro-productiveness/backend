package com.igorgorbunov3333.timer.service.pomodoro.period;

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

    public Map<YearMonth, List<PomodoroDto>> divide(List<PomodoroDto> pomodoro) {
        LocalDateTime localTime = currentTimeService.getCurrentDateTime();
        YearMonth currentYearMonth = YearMonth.from(localTime);

        LocalDate yearStart = LocalDate.of(currentYearMonth.getYear(), 1, 1);
        LocalDate today = localTime.toLocalDate();

        List<YearMonth> months = new ArrayList<>();

        LocalDate current = yearStart;
        while (!current.isAfter(today)) {
            YearMonth month = YearMonth.from(current);

            months.add(month);

            current = month.atEndOfMonth().plusDays(1L);
        }

        Map<YearMonth, List<PomodoroDto>> periodsByPomodoro = new LinkedHashMap<>();
        for (YearMonth month : months) {
            List<PomodoroDto> monthlyPomodoro = pomodoro.stream()
                    .filter(p -> !p.getStartTime().toLocalDateTime().isBefore(month.atDay(1).atStartOfDay())
                            && !p.getStartTime().toLocalDateTime().isAfter(month.atEndOfMonth().atTime(LocalTime.MAX)))
                    .collect(Collectors.toList());

            periodsByPomodoro.put(month, monthlyPomodoro);
        }

        return periodsByPomodoro;
    }

}
