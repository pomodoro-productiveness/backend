package com.igorgorbunov3333.timer.model.dto.pomodoro.report;

public class WorkTimeStandardReportDto extends AbstractStandardReportDto {

    public WorkTimeStandardReportDto(int standardAmount, int balanceAmount, int differenceAmount, double ratio) {
        super(standardAmount, balanceAmount, differenceAmount, ratio);
    }

}
