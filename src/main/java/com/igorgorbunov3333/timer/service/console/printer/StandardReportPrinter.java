package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.AbstractStandardReportDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.service.console.printer.util.PrintUtil;
import com.igorgorbunov3333.timer.service.console.printer.util.SimplePrinter;
import com.igorgorbunov3333.timer.service.pomodoro.report.PomodoroStandardReporter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class StandardReportPrinter {

    private final PomodoroStandardReporter pomodoroStandardReporter;

    public void print(PeriodDto period, List<PomodoroDto> pomodoro) {
        PomodoroStandardReportDto report = pomodoroStandardReporter.report(period, pomodoro);

        Map<String, AbstractStandardReportDto> reports = new LinkedHashMap<>();
        reports.put("Work", report.getWork());
        reports.put("Education", report.getEducation());
        reports.put("General", report.getAmount());

        SimplePrinter.printParagraph();
        SimplePrinter.print("Pomodoro amount report:");

        int longestReportName = reports.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0)
                + 2;

        int pomodoroStandardAmountMaxLength = 0;
        for (AbstractStandardReportDto reportDto : reports.values()) {
            pomodoroStandardAmountMaxLength = Math.max(pomodoroStandardAmountMaxLength, String.valueOf(reportDto.getStandardAmount()).length());
        }

        int pomodoroActualAmountMaxLength = 0;
        for (AbstractStandardReportDto reportDto : reports.values()) {
            pomodoroActualAmountMaxLength = Math.max(pomodoroActualAmountMaxLength, String.valueOf(reportDto.getActualAmount()).length());
        }

        int pomodoroDifferenceAmountMaxLength = 0;
        for (AbstractStandardReportDto reportDto : reports.values()) {
            pomodoroDifferenceAmountMaxLength = Math.max(pomodoroDifferenceAmountMaxLength, String.valueOf(reportDto.getDifferenceAmount()).length());
        }

        for (Map.Entry<String, AbstractStandardReportDto> entry : reports.entrySet()) {
            String reportName = entry.getKey();
            AbstractStandardReportDto abstractReport = entry.getValue();

            int currentReportNameLength = reportName.length();
            int spacesToPrint = longestReportName - currentReportNameLength;

            SimplePrinter.printWithoutCarriageOffset(reportName + StringUtils.SPACE + "standard:");
            IntStream.range(0, spacesToPrint)
                            .forEach(i -> SimplePrinter.printWithoutCarriageOffset(StringUtils.SPACE));
            SimplePrinter.printWithoutCarriageOffset(String.valueOf(abstractReport.getStandardAmount()));

            int spacesToPrintAfterStandardAmount = pomodoroStandardAmountMaxLength
                    - String.valueOf(abstractReport.getStandardAmount()).length();
            IntStream.range(0, spacesToPrintAfterStandardAmount)
                            .forEach(i -> SimplePrinter.printWithoutCarriageOffset(StringUtils.SPACE));

            SimplePrinter.printWithoutCarriageOffset(PrintUtil.TABULATION);

            SimplePrinter.printWithoutCarriageOffset(String.format("actual amount: %d", abstractReport.getActualAmount()));

            int spacesToPrintAfterActualAmount = pomodoroActualAmountMaxLength
                    - String.valueOf(abstractReport.getActualAmount()).length();
            IntStream.range(0, spacesToPrintAfterActualAmount)
                    .forEach(i -> SimplePrinter.printWithoutCarriageOffset(StringUtils.SPACE));

            SimplePrinter.printWithoutCarriageOffset(PrintUtil.TABULATION);

            SimplePrinter.print(String.format("difference: %d", abstractReport.getDifferenceAmount()));
        }
    }

}
