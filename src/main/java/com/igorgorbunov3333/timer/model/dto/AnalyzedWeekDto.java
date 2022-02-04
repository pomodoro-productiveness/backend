package com.igorgorbunov3333.timer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalyzedWeekDto {

    private final int pomodorosToFinalize;
    private final int pomodorosOverworked;

}
