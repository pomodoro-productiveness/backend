package com.igorgorbunov3333.timer.model.dto.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class MonthlyPomodoroDto {

    private List<WeeklyPomodoroDto> weeklyPomodoro;
    private PeriodDto period;

    public List<PomodoroDto> getPomodoro() {
        return weeklyPomodoro.stream()
                .flatMap(weeklyPomodoroDto -> weeklyPomodoroDto.getPomodoro().stream())
                .collect(Collectors.toList());
    }

}
