package com.igorgorbunov3333.timer.backend.service.mapper;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TagMapper {

    public List<PomodoroTag> mapToEntities(List<PomodoroTagDto> dtos) {
        return dtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    public PomodoroTag mapToEntity(PomodoroTagDto dto) {
        return new PomodoroTag(null, dto.getName(), dto.isRemoved());
    }

    public PomodoroTagDto mapToDto(PomodoroTag pomodoroTag) {
        return new PomodoroTagDto(pomodoroTag.getName(), pomodoroTag.isRemoved());
    }

}
