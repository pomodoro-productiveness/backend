package com.igorgorbunov3333.timer.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class MonthlyPomodoroData {

    private Integer month;

    @Setter
    private List<PomodoroDto> pomodoros;

}
