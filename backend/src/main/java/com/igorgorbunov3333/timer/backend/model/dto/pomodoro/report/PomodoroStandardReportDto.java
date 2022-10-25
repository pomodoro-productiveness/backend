package com.igorgorbunov3333.timer.backend.model.dto.pomodoro.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PomodoroStandardReportDto {

    private final GeneralAmountStandardReportDto amount;
    private final EducationTimeStandardReportDto education;
    private final WorkTimeStandardReportDto work;

}
