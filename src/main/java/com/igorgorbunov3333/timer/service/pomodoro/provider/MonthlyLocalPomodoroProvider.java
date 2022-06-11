package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MonthlyLocalPomodoroProvider extends LocalPomodoroProvider {

    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;

    public Map<LocalDate, List<PomodoroDto>> provide() {
        Pair<ZonedDateTime, ZonedDateTime> startEndTimePair = currentTimeService.getCurrentDayPeriod();
        List<PomodoroDto> pomodoros = provide(startEndTimePair.getFirst().minusMonths(1), startEndTimePair.getSecond(), null);
        Map<LocalDate, List<PomodoroDto>> pomodorosByDates = pomodoros.stream()
                .collect(Collectors.groupingBy(pomodoro -> pomodoro.getStartTime().toLocalDate()));

        return new TreeMap<>(pomodorosByDates);
    }

}
