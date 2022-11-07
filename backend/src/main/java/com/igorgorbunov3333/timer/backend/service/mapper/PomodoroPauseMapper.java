package com.igorgorbunov3333.timer.backend.service.mapper;

import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroPauseDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.PomodoroPause;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PomodoroPauseMapper {

    List<PomodoroPause> toEntities(List<PomodoroPauseDto> dtos);

    PomodoroPause toEntity(PomodoroPauseDto dto);

}
