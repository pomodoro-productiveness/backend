package com.igorgorbunov3333.timer.console.service.printer;

import com.igorgorbunov3333.timer.console.rest.dto.PeriodDto;
import com.igorgorbunov3333.timer.console.rest.dto.tag.report.TagDurationReportRowDto;
import com.igorgorbunov3333.timer.console.service.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.console.service.tag.report.TagReportComponent;
import com.igorgorbunov3333.timer.console.service.util.SecondsFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class TagDurationReportPrinter {

    private final TagReportComponent tagDurationReportsComposer;

    public void print(PeriodDto period) {
        LocalDate start = period.getStart().toLocalDate();
        LocalDate end = period.getEnd().toLocalDate();
        print(start, end);
    }

    public void print(LocalDate from, LocalDate to) {
        List<TagDurationReportRowDto> tagDurationReportRows = tagDurationReportsComposer.getTagDurationReport(from, to)
                .getRows();

        SimplePrinter.printParagraph();
        SimplePrinter.print("Duration by tags report:");

        List<ReportRow> reportRows = new ArrayList<>();
        for (TagDurationReportRowDto tagDurationReportRowItem : tagDurationReportRows) {
            String mainTag = tagDurationReportRowItem.getTag();
            String mainTagDuration = SecondsFormatter.formatInHours(tagDurationReportRowItem.getDuration());

            reportRows.add(new ReportRow(mainTag, mainTagDuration, true, List.of()));

            List<TagDurationReportRowDto> neighbouringTagDurations = tagDurationReportRowItem.getMappedRows();

            if (!CollectionUtils.isEmpty(neighbouringTagDurations)) {
                for (TagDurationReportRowDto neighboringTag : neighbouringTagDurations) {
                    String tag = neighboringTag.getTag();
                    String neighboringTagDuration = SecondsFormatter.formatInHours(neighboringTag.getDuration());

                    reportRows.add(new ReportRow("-".repeat(4) + tag, neighboringTagDuration, false, buildSubRows(neighboringTag.getMappedRows(), 2)));
                }
            }
        }

        int maxTagLength = reportRows.stream()
                .mapToInt(row -> row.getTagRow().length())
                .max()
                .orElse(0)
                + 6;

        String space = StringUtils.SPACE;
        String dash = "-";
        for (ReportRow row : reportRows) {
            printRow(maxTagLength, space, dash, row);
        }
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
                        false, buildSubRows(row.getMappedRows(), nestingLevel + 1));
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
