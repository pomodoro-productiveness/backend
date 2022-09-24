package com.igorgorbunov3333.timer.model.dto.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WeeklyPomodoroDto {

    private final List<DailyPomodoroDto> pomodoro;
    private final PeriodDto period;

    public static WeeklyPomodoroDto buildEmpty() {
        return new WeeklyPomodoroDto(List.of(), null);
    }

}
