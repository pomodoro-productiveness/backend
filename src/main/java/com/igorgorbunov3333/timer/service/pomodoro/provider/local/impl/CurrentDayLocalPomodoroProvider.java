package com.igorgorbunov3333.timer.service.pomodoro.provider.local.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.provider.local.LocalPomodoroProvider;
import com.igorgorbunov3333.timer.service.pomodoro.time.calculator.enums.PomodoroPeriod;
import com.igorgorbunov3333.timer.service.tag.TagService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CurrentDayLocalPomodoroProvider implements LocalPomodoroProvider {

    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;
    @Getter
    private final TagService tagService;

    @Override
    public List<PomodoroDto> provide(String tag) {
        Pair<ZonedDateTime, ZonedDateTime> startEndTimePair = currentTimeService.getCurrentDayPeriod();

        return provide(startEndTimePair.getFirst(), startEndTimePair.getSecond(), tag);
    }

    @Override
    public PomodoroPeriod pomodoroPeriod() {
        return PomodoroPeriod.DAY;
    }

}
