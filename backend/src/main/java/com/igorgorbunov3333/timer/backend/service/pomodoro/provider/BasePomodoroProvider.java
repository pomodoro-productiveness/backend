package com.igorgorbunov3333.timer.backend.service.pomodoro.provider;

import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

public interface BasePomodoroProvider {

    PomodoroRepository getPomodoroRepository();
    PomodoroMapper getPomodoroMapper();

    default List<PomodoroDto> provide(ZonedDateTime startRange, ZonedDateTime endRange, String tagName) { //TODO: move this method to another class to not expose it as it used only by providers
        if (tagName == null) {
            return getPomodoroRepository().findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(startRange, endRange).stream()
                    .map(getPomodoroMapper()::toDto)
                    .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                    .toList();
        }

        return getPomodoroRepository().findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(startRange, endRange).stream()
                .filter(pomodoro -> filterByTagAndAllChildTags(pomodoro, tagName))
                .map(getPomodoroMapper()::toDto)
                .sorted(Comparator.comparing(PomodoroDto::getStartTime))
                .toList();
    }

    private boolean filterByTagAndAllChildTags(Pomodoro pomodoro, String tagName) {
        if (!CollectionUtils.isEmpty(pomodoro.getTags())) {
            return pomodoro.getTags().stream()
                    .anyMatch(p -> tagName.equals(p.getName()));
        }

        return false;
    }

}
