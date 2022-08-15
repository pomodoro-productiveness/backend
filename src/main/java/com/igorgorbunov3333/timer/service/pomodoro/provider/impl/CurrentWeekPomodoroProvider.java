package com.igorgorbunov3333.timer.service.pomodoro.provider.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.period.CurrentWeekDaysProvidable;
import com.igorgorbunov3333.timer.service.pomodoro.provider.PomodoroProvider;
import com.igorgorbunov3333.timer.service.tag.TagService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
@Service
@AllArgsConstructor
public class CurrentWeekPomodoroProvider implements PomodoroProvider, CurrentWeekDaysProvidable {

    private final PomodoroRepository pomodoroRepository;
    private final PomodoroMapper pomodoroMapper;
    private final CurrentTimeService currentTimeService;
    private final TagService tagService;

    @Override
    @Transactional(readOnly = true)
    public List<PomodoroDto> provide(String pomodoroTag) {
        LocalDate startDayOfWeek = provideStartDayOfWeek();

        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime start = startDayOfWeek.atStartOfDay().atZone(currentZoneId);
        ZonedDateTime end = currentTimeService.getCurrentDateTime()
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(currentZoneId);

        return provide(start, end, pomodoroTag);
    }

    private LocalDate provideStartDayOfWeek() {
        LocalDate currentDay = getCurrentTimeService().getCurrentDateTime().toLocalDate();

        return currentDay.with(DayOfWeek.MONDAY);
    }

    public Map<DayOfWeek, List<PomodoroDto>> provideCurrentWeekPomodorosByDays() {
        List<PomodoroDto> weeklyPomodoros = provide(null);
        if (weeklyPomodoros.isEmpty()) {
            return Map.of();
        }
        Map<DayOfWeek, List<PomodoroDto>> daysOfWeekToPomodoros = getDaysOfWeekToPomodoros(weeklyPomodoros);
        return new TreeMap<>(daysOfWeekToPomodoros);
    }

    private Map<DayOfWeek, List<PomodoroDto>> getDaysOfWeekToPomodoros(List<PomodoroDto> weeklyPomodoros) {
        List<DayOfWeek> daysOfWeek = provideDaysOfCurrentWeek();

        Map<DayOfWeek, List<PomodoroDto>> dayOfWeekToPomodoros = weeklyPomodoros.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().getDayOfWeek()));

        return daysOfWeek.stream()
                .map(dayOfWeek -> new AbstractMap.SimpleEntry<>(
                        dayOfWeek,
                        getDailyPomodoro(dayOfWeekToPomodoros, dayOfWeek)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<PomodoroDto> getDailyPomodoro(Map<DayOfWeek, List<PomodoroDto>> dayOfWeekToPomodoro,
                                               DayOfWeek dayOfWeek) {
        return dayOfWeekToPomodoro.computeIfAbsent(dayOfWeek, k -> new ArrayList<>());
    }

}
