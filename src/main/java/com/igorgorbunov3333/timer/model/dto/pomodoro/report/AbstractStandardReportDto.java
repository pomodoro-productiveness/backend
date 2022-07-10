package com.igorgorbunov3333.timer.model.dto.pomodoro.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractStandardReportDto {

    private final int standardAmount;
    private final int balanceAmount;

}
