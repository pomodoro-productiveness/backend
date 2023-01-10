package com.igorgorbunov3333.timer.model.dto.tag.report;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
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
    private List<TagDurationReportRowDto> children = new ArrayList<>();

    public void addChild(@NonNull TagDurationReportRowDto child) {
        this.children.add(child);
    }

    public List<TagDurationReportRowDto> getAllRows() {
        List<TagDurationReportRowDto> allRows = new ArrayList<>();

        getRows(this, allRows);

        return allRows;
    }

    public void getRows(TagDurationReportRowDto currentRow, List<TagDurationReportRowDto> allRows) {
        if (currentRow != null) {
            allRows.add(currentRow);
        }

        for (TagDurationReportRowDto child : currentRow.getChildren()) {
            getRows(child, allRows);
        }
    }

    public boolean hasChild(@NonNull String tag) {
        return getAllRows().stream()
                .anyMatch(t -> t.getTag().equals(tag));
    }

}
