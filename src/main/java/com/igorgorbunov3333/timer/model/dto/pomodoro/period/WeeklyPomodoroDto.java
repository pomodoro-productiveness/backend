package com.igorgorbunov3333.timer.model.dto.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class WeeklyPomodoroDto {

    private final List<DailyPomodoroDto> dailyPomodoro;
    private final PeriodDto period;

    public static WeeklyPomodoroDto buildEmpty() {
        return new WeeklyPomodoroDto(List.of(), null);
    }

    public List<PomodoroDto> getPomodoro() {
        return dailyPomodoro.stream()
                .flatMap(p -> p.getPomodoro().stream())
                .collect(Collectors.toList());
    }

}