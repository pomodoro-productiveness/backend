package com.igorgorbunov3333.timer.model.dto.tag.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "tag")
@ToString(of = {"tag", "duration"})
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TagDurationReportRowDto {

    @Setter
    private String tag;
    @Setter
    private long duration;

    @Setter
    private List<TagDurationReportRowDto> mappedRows;

    @JsonIgnore
    private TagDurationReportRowDto parentRow;

}
