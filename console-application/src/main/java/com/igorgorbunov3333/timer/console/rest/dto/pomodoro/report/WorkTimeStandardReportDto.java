package com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report;

public class WorkTimeStandardReportDto extends AbstractStandardReportDto {

    public WorkTimeStandardReportDto(int standardAmount, int balanceAmount, int differenceAmount) {
        super(standardAmount, balanceAmount, differenceAmount);
    }

}