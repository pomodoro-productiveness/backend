package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.period.CurrentWeekDaysProvidable;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

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

@Component
@AllArgsConstructor
public class WeeklyLocalPomodoroProvider extends LocalPomodoroProvider implements CurrentWeekDaysProvidable {

    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;
    @Getter
    private final CurrentTimeService currentTimeService;

    public List<PomodoroDto> provideCurrentWeekPomodoros(String pomodoroTag) {
        LocalDate currentDay = currentTimeService.getCurrentDateTime().toLocalDate();
        int currentDayOfWeek = currentDay.getDayOfWeek().getValue();
        LocalDate dayAtStartOfWeek = currentDay.minusDays(currentDayOfWeek - 1);
        ZoneId currentZoneId = ZoneId.systemDefault();
        ZonedDateTime start = dayAtStartOfWeek.atStartOfDay().atZone(currentZoneId);
        ZonedDateTime end = currentDay.atTime(LocalTime.MAX).atZone(currentZoneId);

        return provide(start, end, pomodoroTag);
    }

    public Map<DayOfWeek, List<PomodoroDto>> provideCurrentWeekPomodorosByDays() {
        List<PomodoroDto> weeklyPomodoros = provideCurrentWeekPomodoros(null);
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
                        getDailyPomodoros(dayOfWeekToPomodoros, dayOfWeek)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<PomodoroDto> getDailyPomodoros(Map<DayOfWeek, List<PomodoroDto>> dayOfWeekToPomodoros,
                                                DayOfWeek dayOfWeek) {
        return dayOfWeekToPomodoros.computeIfAbsent(dayOfWeek, k -> new ArrayList<>());
    }

}
