package com.igorgorbunov3333.timer.service.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroByWeekDivider {

    @Getter
    private final CurrentTimeService currentTimeService;

    public Map<PeriodDto, List<PomodoroDto>> divide(PeriodDto monthPeriod, List<PomodoroDto> pomodoro) {
        if (CollectionUtils.isEmpty(pomodoro)) {
            return Map.of();
        }

        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();
        List<PeriodDto> weekPeriods = divideForWeeklyPeriods(monthPeriod, today);

        Map<PeriodDto, List<PomodoroDto>> periods = new LinkedHashMap<>();

        for (PeriodDto currentWeekPeriod : weekPeriods) {
            List<PomodoroDto> currentWeekPomodoro = pomodoro.stream()
                    .filter(p -> !p.getStartTime().toLocalDateTime().isBefore(currentWeekPeriod.getStart())
                            && !p.getEndTime().toLocalDateTime().isAfter(currentWeekPeriod.getEnd()))
                    .collect(Collectors.toList());
            periods.put(currentWeekPeriod, currentWeekPomodoro);
        }

        return periods;
    }

    private List<PeriodDto> divideForWeeklyPeriods(PeriodDto inputPeriod, LocalDate today) {
        LocalDate startDate = inputPeriod.getStart().toLocalDate();
        LocalDate endDate = inputPeriod.getEnd().toLocalDate();

        List<PeriodDto> periods = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            PeriodDto period = getWeekByDate(current);

            if (period.getEnd().toLocalDate().isAfter(today)) {
                period = new PeriodDto(period.getStart(), today.atTime(LocalTime.MAX));
            }

            periods.add(period);

            current = period.getEnd().toLocalDate().plusDays(1L);
        }

        return periods;
    }

    private PeriodDto getWeekByDate(LocalDate date) {
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);

        LocalDateTime periodStart = startOfWeek.atStartOfDay();
        LocalDateTime periodEnd = startOfWeek.plusDays(DayOfWeek.SATURDAY.getValue()).atTime(LocalTime.MAX);

        return new PeriodDto(periodStart, periodEnd);
    }

}
