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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        Map<DayOfWeek, List<PomodoroDto>> daysOfWeekToPomodoros = getDaysOfWeekToPomodoros(currentDayOfWeek, weeklyPomodoros);
        return new TreeMap<>(daysOfWeekToPomodoros);
    }

    private Map<DayOfWeek, List<PomodoroDto>> getDaysOfWeekToPomodoros(int currentDayOfWeek,
                                                                       List<Pomodoro> weeklyPomodoros) {
        Map<DayOfWeek, List<Pomodoro>> dayOfWeekToPomodoros = weeklyPomodoros.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().getDayOfWeek()));
        return IntStream.range(DayOfWeek.MONDAY.getValue(), currentDayOfWeek + 1)
                .boxed()
                .map(DayOfWeek::of)
                .map(dayOfWeek -> new AbstractMap.SimpleEntry<>(
                        dayOfWeek,
                        getDailyPomodoros(dayOfWeekToPomodoros, dayOfWeek)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<PomodoroDto> getDailyPomodoros(Map<DayOfWeek, List<Pomodoro>> dayOfWeekToPomodoros,
                                                DayOfWeek dayOfWeek) {
        List<Pomodoro> dailyPomodoros = dayOfWeekToPomodoros.computeIfAbsent(dayOfWeek, k -> new ArrayList<>());
        return dailyPomodoros.stream()
                .map(pomodoroMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
