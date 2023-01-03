package com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report;

public class GeneralAmountStandardReportDto extends AbstractStandardReportDto {

    public GeneralAmountStandardReportDto(int standardAmount, int balanceAmount, int differenceAmount) {
        super(standardAmount, balanceAmount, differenceAmount);
    }

}