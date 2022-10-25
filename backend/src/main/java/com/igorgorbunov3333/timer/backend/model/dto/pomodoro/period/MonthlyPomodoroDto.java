package com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
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
public class MonthlyPomodoroDto {

    private List<WeeklyPomodoroDto> weeklyPomodoro;
    private PeriodDto period;

    @JsonIgnore
    public List<PomodoroDto> getPomodoro() {
        return weeklyPomodoro.stream()
                .flatMap(weeklyPomodoroDto -> weeklyPomodoroDto.getPomodoro().stream())
                .collect(Collectors.toList());
    }

    public static MonthlyPomodoroDto buildEmpty(PeriodDto period) {
        return new MonthlyPomodoroDto(List.of(), period);
    }

}
