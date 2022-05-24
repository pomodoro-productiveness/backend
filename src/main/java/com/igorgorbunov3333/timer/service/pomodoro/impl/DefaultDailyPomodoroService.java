package com.igorgorbunov3333.timer.service.pomodoro.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.DailyPomodoroService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class DefaultDailyPomodoroService implements DailyPomodoroService {

    private final PomodoroRepository pomodoroRepository;
    private final CurrentTimeService currentTimeService;
    private final PomodoroMapper pomodoroMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PomodoroDto> getDailyPomodoros() {
        Pair<ZonedDateTime, ZonedDateTime> startEndTimePair = currentTimeService.getCurrentDayPeriod();

        List<Pomodoro> pomodoros = pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(
                startEndTimePair.getFirst(), startEndTimePair.getSecond());

        List<PomodoroDto> pomodoroDtos = pomodoroMapper.mapToDto(pomodoros);
        pomodoroDtos.sort(Comparator.comparing(PomodoroDto::getStartTime));
        return pomodoroDtos;
    }

}
