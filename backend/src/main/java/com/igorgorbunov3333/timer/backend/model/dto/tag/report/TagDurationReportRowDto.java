package com.igorgorbunov3333.timer.backend.model.dto.tag.report;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@ToString(of = {"tag", "duration"})
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TagDurationReportRowDto {

    private String tag;
    private long duration;
    private List<TagDurationReportRowDto> mappedRows;

}
