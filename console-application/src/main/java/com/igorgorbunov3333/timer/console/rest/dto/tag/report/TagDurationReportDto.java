package com.igorgorbunov3333.timer.console.rest.dto.tag.report;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TagDurationReportDto {
    
    private List<TagDurationReportRowDto> rows;
    
}