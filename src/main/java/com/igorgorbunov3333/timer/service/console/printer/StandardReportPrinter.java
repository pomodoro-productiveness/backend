package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.EducationTimeStandardReportDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.GeneralAmountStandardReportDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.PomodoroStandardReportDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.report.WorkTimeStandardReportDto;
import com.igorgorbunov3333.timer.service.pomodoro.report.PomodoroStandardReporter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

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

        printerService.printParagraph();
        WorkTimeStandardReportDto workTimeReport = report.getWork();
        int workStandard = workTimeReport.getStandardAmount();
        int workBalance = workTimeReport.getBalanceAmount();
        printerService.print(String.format("Work standard - [%d], balance - [%d]", workStandard, workBalance));

        EducationTimeStandardReportDto educationTimeStandardReportDto = report.getEducation();
        int educationStandard = educationTimeStandardReportDto.getStandardAmount();
        int educationBalance = educationTimeStandardReportDto.getBalanceAmount();
        printerService.print(String.format("Education standard - [%d], balance - [%d]", educationStandard, educationBalance));

        GeneralAmountStandardReportDto generalAmountStandardReportDto = report.getAmount();
        int generalStandard = generalAmountStandardReportDto.getStandardAmount();
        int generalBalance = generalAmountStandardReportDto.getBalanceAmount();
        printerService.print(String.format("General standard - [%d], balance - [%d]", generalStandard, generalBalance));
    }

}
