package com.igorgorbunov3333.timer.model.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TagDurationReportDto {

    private final List<SingleTagDurationDto> tagInfo;

}
