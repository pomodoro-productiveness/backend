package com.igorgorbunov3333.timer.service.pomodoro.provider.impl;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.service.pomodoro.provider.PomodoroProvider;
import com.igorgorbunov3333.timer.service.tag.TagService;
import com.igorgorbunov3333.timer.service.util.CurrentTimeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CurrentYearPomodoroProvider implements PomodoroProvider {

    private final CurrentTimeService currentTimeService;
    @Getter
    private final PomodoroRepository pomodoroRepository;
    @Getter
    private final PomodoroMapper pomodoroMapper;
    @Getter
    private final TagService tagService;

    @Override
    public List<PomodoroDto> provide(String tag) {
        LocalDate today = currentTimeService.getCurrentDateTime().toLocalDate();

        ZonedDateTime start = today.withDayOfYear(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        return provide(start, end, tag);
    }

}
