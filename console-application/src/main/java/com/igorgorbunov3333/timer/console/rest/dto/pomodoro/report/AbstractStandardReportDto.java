package com.igorgorbunov3333.timer.console.rest.dto.pomodoro.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractStandardReportDto {

    private final int standardAmount;
    private final int differenceAmount;
    private final int actualAmount;

    public double getRatio() {
        double ratio = 0.0;

        if (standardAmount > 0) {
            ratio = (double) actualAmount / standardAmount;
        }

        return ratio;
    }

}