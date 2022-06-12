package com.igorgorbunov3333.timer.service.pomodoro.provider;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
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

        return getPomodoroRepository().findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(startRange, endRange).stream()
                .filter(pomodoro -> filterByTag(pomodoro, tagName))
                .map(getPomodoroMapper()::mapToDto)
                .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                .collect(Collectors.toList());
    }

    private boolean filterByTag(Pomodoro pomodoro, String tagName) {
        PomodoroTag tag = pomodoro.getTag();

        if (tag == null) {
            return false;
        }

        return tagName.equals(tag.getName()) || pomodoroParentTagEqualToTag(tag.getParent(), tagName);
    }

    private boolean pomodoroParentTagEqualToTag(PomodoroTag parentTag, String tagName) {
        return parentTag != null && tagName.equals(parentTag.getName());
    }

}
