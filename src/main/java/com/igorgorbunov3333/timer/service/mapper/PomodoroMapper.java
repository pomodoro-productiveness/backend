package com.igorgorbunov3333.timer.service.mapper;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroMapper {

    private final TagMapper tagMapper;

    public PomodoroDto mapToDto(Pomodoro pomodoro) {
        return new PomodoroDto(
                pomodoro.getId(),
                pomodoro.getStartTime(),
                pomodoro.getEndTime(),
                pomodoro.isSavedAutomatically(),
                List.of(),
                mapPomodoroTagToDto(pomodoro.getTags())
        );
    }

    private List<PomodoroTagDto> mapPomodoroTagToDto(List<PomodoroTag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(tagMapper::mapToDto)
                .sorted(Comparator.comparing(tag -> tag.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

}
