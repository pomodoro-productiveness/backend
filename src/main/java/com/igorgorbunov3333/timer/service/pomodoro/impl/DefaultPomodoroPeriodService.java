package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.PomodoroPeriodService;
import com.igorgorbunov3333.timer.service.util.CurrentDayService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultPomodoroPeriodService implements PomodoroPeriodService {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;
    private final CurrentDayService currentDayService;

    @Override
    public Map<DayOfWeek, List<PomodoroDto>> getCurrentWeekPomodoros() {
        LocalDate currentDay = currentDayService.getCurrentDay();
        int currentDayOfWeek = currentDay.getDayOfWeek().getValue();
        LocalDate dayAtStartOfWeek = currentDay.minusDays(currentDayOfWeek - 1);
        LocalDateTime start = dayAtStartOfWeek.atStartOfDay();
        LocalDateTime end = currentDay.atTime(LocalTime.MAX);
        List<Pomodoro> weeklyPomodoros = pomodoroRepository.findByStartTimeAfterAndEndTimeBefore(start, end);
        if (weeklyPomodoros.isEmpty()) {
            return Map.of();
        }
        Map<DayOfWeek, List<Pomodoro>> dayOfWeekToPomodoros = weeklyPomodoros.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().getDayOfWeek()));
        Map<DayOfWeek, List<PomodoroDto>> dayOfWeekToPomodoroDtos = dayOfWeekToPomodoros.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), pomodoroMapper.mapToDto(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new TreeMap<>(dayOfWeekToPomodoroDtos);
    }

}
