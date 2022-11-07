package com.igorgorbunov3333.timer.backend.service.mapper;

import com.igorgorbunov3333.timer.backend.model.dto.tag.PomodoroTagGroupDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroTagGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PomodoroTagGroupMapper {

    PomodoroTagGroupDto toDto(PomodoroTagGroup tagGroups);

}
