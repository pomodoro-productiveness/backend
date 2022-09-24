package com.igorgorbunov3333.timer.model.dto.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DailyPomodoroDto {

    private final List<PomodoroDto> pomodoro;
    private final boolean dayOff;
    private final DayOfWeek dayOfWeek;
    private final LocalDate date;

}
