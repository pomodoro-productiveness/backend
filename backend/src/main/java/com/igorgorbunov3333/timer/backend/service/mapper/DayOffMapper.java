package com.igorgorbunov3333.timer.backend.service.mapper;

import com.igorgorbunov3333.timer.backend.model.dto.dayoff.DayOffDto;
import com.igorgorbunov3333.timer.backend.model.entity.dayoff.DayOff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DayOffMapper {

    List<DayOff> toEntities(List<DayOffDto> dtos);

    DayOff toEntity(DayOffDto dto);

}
