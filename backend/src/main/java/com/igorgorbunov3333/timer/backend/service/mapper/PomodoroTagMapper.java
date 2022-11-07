package com.igorgorbunov3333.timer.backend.service.mapper;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PomodoroTagMapper {

    List<PomodoroTag> toEntities(List<PomodoroTagDto> dtos);

    PomodoroTag toEntity(PomodoroTagDto dto);

    PomodoroTagDto toDto(PomodoroTag pomodoroTag);

}
