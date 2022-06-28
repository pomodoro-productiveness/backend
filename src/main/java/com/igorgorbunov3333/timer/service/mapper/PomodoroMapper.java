package com.igorgorbunov3333.timer.service.mapper;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroPause;
import com.igorgorbunov3333.timer.model.entity.pomodoro.PomodoroTag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
                mapPomodoroPausesToDtos(pomodoro.getPomodoroPauses()),
                mapPomodoroTagToDto(pomodoro.getTags())
        );
    }

    public List<Pomodoro> mapToEntity(List<PomodoroDto> pomodoroDtos) {
        return pomodoroDtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    private List<PomodoroPauseDto> mapPomodoroPausesToDtos(List<PomodoroPause> pomodoroPauses) {
        return pomodoroPauses.stream()
                .map(this::mapPomodoroPauseToDto)
                .collect(Collectors.toList());
    }

    private PomodoroPauseDto mapPomodoroPauseToDto(PomodoroPause pomodoroPause) {
        return new PomodoroPauseDto(pomodoroPause.getStartTime(), pomodoroPause.getEndTime());
    }

    private List<PomodoroTagDto> mapPomodoroTagToDto(List<PomodoroTag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(tagMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private Pomodoro mapToEntity(PomodoroDto dto) {
        return new Pomodoro(
                null,
                dto.getStartTime(),
                dto.getEndTime(),
                dto.isSavedAutomatically(),
                mapPomodoroPausesToEntities(dto.getPomodoroPauses()),
                mapPomodoroTagToEntity(dto.getTags())
        );
    }

    private List<PomodoroPause> mapPomodoroPausesToEntities(List<PomodoroPauseDto> dtos) {
        return dtos.stream()
                .map(this::mapPomodoroPauseToEntity)
                .collect(Collectors.toList());
    }

    private PomodoroPause mapPomodoroPauseToEntity(PomodoroPauseDto dto) {
        return new PomodoroPause(null, dto.getStartTime(), dto.getEndTime(), null);
    }

    private List<PomodoroTag> mapPomodoroTagToEntity(List<PomodoroTagDto> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return null;
        }

        return tagMapper.mapToEntities(dtos);
    }

}
