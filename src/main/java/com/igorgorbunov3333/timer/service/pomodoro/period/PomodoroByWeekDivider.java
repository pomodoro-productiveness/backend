package com.igorgorbunov3333.timer.service.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroByWeekDivider {

    @Getter
    private final CurrentTimeService currentTimeService;

    public Map<PeriodDto, List<PomodoroDto>> divide(List<PomodoroDto> pomodoro) {
        if (CollectionUtils.isEmpty(pomodoro)) {
            return Map.of();
        }

        PomodoroDto weekFirstPomodoro = pomodoro.get(0);
        LocalDate lastPomodoroDate = pomodoro.get(pomodoro.size() - 1).getStartTime().toLocalDate();

        LocalDate currentStartOfWeek = weekFirstPomodoro.getStartTime().toLocalDate();

        Map<PeriodDto, List<PomodoroDto>> periods = new LinkedHashMap<>();
        while (true) {
            int currentDayWeek = weekFirstPomodoro.getStartTime().getDayOfWeek().getValue();
            LocalDate currentEndOfWeek = currentStartOfWeek.plusDays(7 - currentDayWeek);
            if (currentEndOfWeek.isAfter(lastPomodoroDate)) {
                currentEndOfWeek = lastPomodoroDate;
            }

            LocalDate startDatePeriod = currentStartOfWeek;
            LocalDate endDatePeriod = currentEndOfWeek;
            List<PomodoroDto> weeklyPomodoro = pomodoro.stream()
                    .filter(p -> !p.getStartTime().toLocalDate().isBefore(startDatePeriod)
                            && !p.getStartTime().toLocalDate().isAfter(endDatePeriod))
                    .collect(Collectors.toList());

            PeriodDto currentPeriod = new PeriodDto(currentStartOfWeek.atStartOfDay(), currentEndOfWeek.atTime(LocalTime.MAX));
            periods.put(currentPeriod, weeklyPomodoro);

            currentStartOfWeek = currentEndOfWeek.plusDays(1L);
            if (currentStartOfWeek.isAfter(lastPomodoroDate)) {
                break;
            }
            weekFirstPomodoro = pomodoro.get(pomodoro.indexOf(weeklyPomodoro.get(weeklyPomodoro.size() - 1)) + 1);
        }

        return periods;
    }

}
