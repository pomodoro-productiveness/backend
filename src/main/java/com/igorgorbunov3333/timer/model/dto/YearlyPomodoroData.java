package com.igorgorbunov3333.timer.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "year")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class YearlyPomodoroData {

    private Integer year;
    private List<MonthlyPomodoroData> monthlyPomodoroData;

}
