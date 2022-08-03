package com.igorgorbunov3333.timer.model.dto.tag.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class TagDurationReportRowDto {

    private final String tag;
    private final long duration;

}
