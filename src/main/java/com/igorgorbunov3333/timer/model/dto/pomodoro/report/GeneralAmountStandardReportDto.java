package com.igorgorbunov3333.timer.model.dto.pomodoro.report;

public class GeneralAmountStandardReportDto extends AbstractStandardReportDto {

    public GeneralAmountStandardReportDto(int standardAmount, int balanceAmount, int differenceAmount, double ratio) {
        super(standardAmount, balanceAmount, differenceAmount, ratio);
    }

}
