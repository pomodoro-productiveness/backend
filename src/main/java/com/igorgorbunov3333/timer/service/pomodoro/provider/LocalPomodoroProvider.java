package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.service.mapper.PomodoroMapper;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//TODO: refactor
public abstract class LocalPomodoroProvider {

    public abstract PomodoroRepository getPomodoroRepository();
    public abstract PomodoroMapper getPomodoroMapper();

    public List<PomodoroDto> provide(ZonedDateTime startRange, ZonedDateTime endRange, String tagName) {
        if (startRange == null && endRange == null) {
            return getPomodoroRepository().findAll().stream()
                    .map(getPomodoroMapper()::mapToDto)
                    .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                    .collect(Collectors.toList());
        }

        if (tagName == null) {
            return getPomodoroRepository().findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(startRange, endRange).stream()
                    .map(getPomodoroMapper()::mapToDto)
                    .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                    .collect(Collectors.toList());
        }

        return getPomodoroRepository().findByStartTimeAfterAndEndTimeBeforeAndTagNameOrderByStartTime(startRange, endRange, tagName).stream()
                .map(getPomodoroMapper()::mapToDto)
                .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                .collect(Collectors.toList());
    }

}
