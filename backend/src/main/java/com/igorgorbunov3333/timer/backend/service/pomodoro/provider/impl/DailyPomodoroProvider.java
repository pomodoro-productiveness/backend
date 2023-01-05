package com.igorgorbunov3333.timer.backend.service.pomodoro.provider.impl;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.backend.service.pomodoro.provider.BasePomodoroProvider;
import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DailyPomodoroProvider implements BasePomodoroProvider {

    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;

    public List<PomodoroDto> provideForCurrentDay(String tag) {
        Pair<ZonedDateTime, ZonedDateTime> startEndTimePair = currentTimeService.getCurrentDayPeriod();

        return provide(startEndTimePair.getFirst(), startEndTimePair.getSecond(), tag);
    }

}
