package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.AbstractStandardReportDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.service.pomodoro.report.PomodoroStandardReporter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class StandardReportPrinter {

    private final PomodoroStandardReporter pomodoroStandardReporter;
    private final PrinterService printerService;

    public void print(PeriodDto period, List<PomodoroDto> pomodoro) {
        if (CollectionUtils.isEmpty(pomodoro)) {
            return;
        }

        PomodoroStandardReportDto report = pomodoroStandardReporter.report(period, pomodoro);

        Map<String, AbstractStandardReportDto> reports = new LinkedHashMap<>();
        reports.put("Work", report.getWork());
        reports.put("Education", report.getEducation());
        reports.put("General", report.getAmount());

        printerService.printParagraph();

        for (Map.Entry<String, AbstractStandardReportDto> entry : reports.entrySet()) {
            String reportName = entry.getKey();
            AbstractStandardReportDto abstractReport = entry.getValue();

            printerService.print(String.format(reportName + StringUtils.SPACE + "standard: %d, actual amount: %d, difference: %d",
                    abstractReport.getStandardAmount(),
                    abstractReport.getActualAmount(),
                    abstractReport.getDifferenceAmount()));
        }
    }

}
