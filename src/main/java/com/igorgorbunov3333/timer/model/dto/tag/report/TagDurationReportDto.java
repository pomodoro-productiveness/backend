package com.igorgorbunov3333.timer.model.dto.tag.report;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class TagDurationReportDto {

    private final TagDurationReportRowDto mainTagReportRow;
    private final List<TagDurationReportRowDto> mappedTagsReportRows;

}
