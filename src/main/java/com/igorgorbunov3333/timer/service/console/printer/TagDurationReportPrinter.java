package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.tag.report.TagDurationReportsComposer;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

//TODO: refactor
@Component
@AllArgsConstructor
public class TagDurationReportPrinter {

    private final TagDurationReportsComposer tagDurationReportsComposer;

    public void print(List<PomodoroDto> pomodoro) {
        List<TagDurationReportDto> tagDurationReports = tagDurationReportsComposer.compose(pomodoro);

        SimplePrinter.printParagraph();
        SimplePrinter.print("Duration by tags report:");

        List<ReportRow> reportRows = new ArrayList<>();
        for (TagDurationReportDto tagDurationReportItem : tagDurationReports) {
            String mainTag = tagDurationReportItem.getMainTagReportRow().getTag();
            String mainTagDuration = SecondsFormatter.formatInHours(tagDurationReportItem.getMainTagReportRow().getDuration());

            reportRows.add(new ReportRow(mainTag, mainTagDuration, true, List.of()));

            List<TagDurationReportRowDto> neighbouringTagDurations = tagDurationReportItem.getMappedTagsReportRows();

            if (!CollectionUtils.isEmpty(neighbouringTagDurations)) {
                for (TagDurationReportRowDto neighboringTag : neighbouringTagDurations) {
                    String tag = neighboringTag.getTag();
                    String neighboringTagDuration = SecondsFormatter.formatInHours(neighboringTag.getDuration());

                    reportRows.add(new ReportRow("-".repeat(4) + tag, neighboringTagDuration, false, buildSubRows(neighboringTag.getSubRows(), 2)));
                }
            }
        }

        int maxTagLength = reportRows.stream()
                .mapToInt(row -> row.getTagRow().length())
                .max()
                .orElse(0)
                + 1;

        String space = StringUtils.SPACE;
        String dash = "-";
        for (ReportRow row : reportRows) {
            printRow(maxTagLength, space, dash, row);
        }

        SimplePrinter.printParagraph();
    }

    private void printRow(int maxTagLength, String space, String dash, ReportRow row) {
        if (row.mainTag) {
            SimplePrinter.printParagraph();
        }

        SimplePrinter.printWithoutCarriageOffset(row.getTagRow());

        int additionalSpaces = maxTagLength - row.getTagRow().length();

        String indentToRepeat = row.mainTag ? dash : space;
        SimplePrinter.printWithoutCarriageOffset(indentToRepeat.repeat(Math.max(0, additionalSpaces)));

        SimplePrinter.print(row.durationRow);

        for (ReportRow subRow : row.getSubRows()) {
            printRow(maxTagLength, space, dash, subRow);
        }
    }

    private List<ReportRow> buildSubRows(List<TagDurationReportRowDto> rows, int nestingLevel) {
        List<ReportRow> reportRows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rows)) {
            for (TagDurationReportRowDto row : rows) {
                ReportRow newRow = new ReportRow("-".repeat(4 * nestingLevel) + row.getTag(), SecondsFormatter.formatInHours(row.getDuration()),
                        false, buildSubRows(row.getSubRows(), nestingLevel + 1));
                reportRows.add(newRow);
            }
        }

        return reportRows;
    }

    @Getter
    @AllArgsConstructor
    private static class ReportRow {

        private final String tagRow;
        private final String durationRow;
        private final boolean mainTag;
        private final List<ReportRow> subRows;

    }

}
