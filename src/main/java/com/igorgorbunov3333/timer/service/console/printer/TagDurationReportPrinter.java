package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.SingleTagDurationDto;
import com.igorgorbunov3333.timer.model.dto.tag.TagDurationReportDto;
import com.igorgorbunov3333.timer.service.console.printer.impl.DefaultPrinterService;
import com.igorgorbunov3333.timer.service.tag.report.TagDurationReporter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class TagDurationReportPrinter {

    private final TagDurationReporter tagDurationReporter;
    private final PrinterService printerService;

    public void print(List<PomodoroDto> pomodoro) {
        TagDurationReportDto tagDurationReport = tagDurationReporter.report(pomodoro);

        printerService.printParagraph();
        printerService.print("Duration by tags report:");

        Set<String> tags = tagDurationReport.getTagInfo().stream()
                .map(SingleTagDurationDto::getTag)
                .collect(Collectors.toSet());

        int maxTagLength = tags.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0)
                + 1;

        for (SingleTagDurationDto singleTagDuration : tagDurationReport.getTagInfo()) {
            printerService.printWithoutCarriageOffset(singleTagDuration.getTag() + ":");

            int currentLength = singleTagDuration.getTag().length();
            int spacesToPrint = maxTagLength - currentLength;
            IntStream.range(0, spacesToPrint)
                            .forEach(i -> printerService.printWithoutCarriageOffset(StringUtils.SPACE));
            printerService.print(DefaultPrinterService.TABULATION + singleTagDuration.getDuration());
        }
    }

}
