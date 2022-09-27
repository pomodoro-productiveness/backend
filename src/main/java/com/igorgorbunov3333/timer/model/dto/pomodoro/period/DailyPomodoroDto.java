package com.igorgorbunov3333.timer.model.dto.pomodoro.period;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DailyPomodoroDto {

    private List<PomodoroDto> pomodoro;
    private boolean dayOff;
    private DayOfWeek dayOfWeek;
    private LocalDate date;

}
