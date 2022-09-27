package com.igorgorbunov3333.timer.model.dto.pomodoro.period;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igorgorbunov3333.timer.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class WeeklyPomodoroDto {

    private List<DailyPomodoroDto> dailyPomodoro;
    private PeriodDto period;

    public static WeeklyPomodoroDto buildEmpty() {
        return new WeeklyPomodoroDto(List.of(), null);
    }

    @JsonIgnore
    public List<PomodoroDto> getPomodoro() {
        return dailyPomodoro.stream()
                .flatMap(p -> p.getPomodoro().stream())
                .collect(Collectors.toList());
    }

}
