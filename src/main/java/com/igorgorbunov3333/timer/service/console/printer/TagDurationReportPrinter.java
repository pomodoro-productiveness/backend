package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.tag.report.TagDurationReporter;
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

    private final TagDurationReporter tagDurationReporter;

    public void print(List<PomodoroDto> pomodoro) {
        List<TagDurationReportDto> tagDurationReports = tagDurationReporter.report(pomodoro);

        SimplePrinter.printParagraph();
        SimplePrinter.print("Duration by tags report:");

        List<ReportRow> reportRows = new ArrayList<>();
        for (TagDurationReportDto tagDurationReportItem : tagDurationReports) {
            String mainTag = tagDurationReportItem.getMainTagReportRow().getTag();
            String mainTagDuration = SecondsFormatter.formatInHours(tagDurationReportItem.getMainTagReportRow().getDuration());

            reportRows.add(new ReportRow(mainTag, mainTagDuration, true));

            List<TagDurationReportRowDto> neighbouringTagDurations = tagDurationReportItem.getMappedTagsReportRows();

            if (CollectionUtils.isEmpty(neighbouringTagDurations)) {
                break;
            }

            for (TagDurationReportRowDto neighboringTag : neighbouringTagDurations) {
                String tag = neighboringTag.getTag();
                String neighboringTagDuration = SecondsFormatter.formatInHours(neighboringTag.getDuration());

                reportRows.add(new ReportRow("-".repeat(4) + tag, neighboringTagDuration, false));
            }
        }

        int maxTagLength = reportRows.stream()
                .mapToInt(row -> row.getTagRow().length())
                .max()
                .orElse(0)
                + 1;

        String spaces = StringUtils.SPACE;
        for (ReportRow row : reportRows) {
            if (row.mainTag) {
                SimplePrinter.printParagraph();
            }

            SimplePrinter.printWithoutCarriageOffset(row.getTagRow());

            int additionalSpaces = maxTagLength - row.getTagRow().length();

            SimplePrinter.printWithoutCarriageOffset(spaces.repeat(Math.max(0, additionalSpaces)));

            SimplePrinter.print(row.durationRow);
        }

    }

    @Getter
    @AllArgsConstructor
    private static class ReportRow {

        private final String tagRow;
        private final String durationRow;
        private final boolean mainTag;

    }

}
