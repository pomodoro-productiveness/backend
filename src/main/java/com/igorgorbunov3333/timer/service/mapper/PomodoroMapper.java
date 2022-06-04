package com.igorgorbunov3333.timer.service.mapper;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import com.igorgorbunov3333.timer.model.entity.PomodoroPause;
import com.igorgorbunov3333.timer.model.entity.PomodoroTag;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PomodoroMapper {

    public List<PomodoroDto> mapToDto(List<Pomodoro> pomodoros) {
        return pomodoros.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PomodoroDto mapToDto(Pomodoro pomodoro) {
        return new PomodoroDto(
                pomodoro.getId(),
                pomodoro.getStartTime(),
                pomodoro.getEndTime(),
                pomodoro.isSavedAutomatically(),
                mapPomodoroPausesToDtos(pomodoro.getPomodoroPauses()),
                mapPomodoroTagToDto(pomodoro.getTag())
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

    private PomodoroTagDto mapPomodoroTagToDto(PomodoroTag tag) {
        if (tag == null) {
            return null;
        }

        List<PomodoroTagDto> tagChildren = mapChildTagsToDtos(tag.getChildren());

        return new PomodoroTagDto(tag.getName(), tagChildren, tag.isRemoved());
    }

    private List<PomodoroTagDto> mapChildTagsToDtos(List<PomodoroTag> childrenTags) {
        if (CollectionUtils.isEmpty(childrenTags)) {
            return List.of();
        }

        return childrenTags.stream()
                .map(childTag -> new PomodoroTagDto(childTag.getName(), List.of(), childTag.isRemoved()))
                .collect(Collectors.toList());
    }

    private Pomodoro mapToEntity(PomodoroDto dto) {
        return new Pomodoro(
                null,
                dto.getStartTime(),
                dto.getEndTime(),
                dto.isSavedAutomatically(),
                mapPomodoroPausesToEntities(dto.getPomodoroPauses()),
                mapPomodoroTagToEntity(dto.getTag())
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

    private PomodoroTag mapPomodoroTagToEntity(PomodoroTagDto dto) {
        if (dto == null) {
            return null;
        }

        List<PomodoroTagDto> childDtoTags = getNotNullableTags(dto);
        List<PomodoroTag> childTags = mapToChildEntities(childDtoTags);

        return new PomodoroTag(null, dto.getName(), null, childTags, dto.isRemoved());
    }

    private List<PomodoroTagDto> getNotNullableTags(PomodoroTagDto dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getChildren())) {
            return List.of();
        }

        return dto.getChildren();
    }

    private List<PomodoroTag> mapToChildEntities(List<PomodoroTagDto> children) {
        return children.stream()
                .map(child -> new PomodoroTag(null, child.getName(), null, Collections.emptyList(), child.isRemoved()))
                .collect(Collectors.toList());
    }

}
