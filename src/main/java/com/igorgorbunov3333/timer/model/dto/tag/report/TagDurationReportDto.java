package com.igorgorbunov3333.timer.model.dto.tag.report;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TagDurationReportDto {

    private TagDurationReportRowDto mainTagReportRow;
    private List<TagDurationReportRowDto> mappedTagsReportRows;

}
