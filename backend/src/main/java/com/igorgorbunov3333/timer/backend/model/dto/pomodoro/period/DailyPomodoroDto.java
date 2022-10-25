package com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period;

import com.igorgorbunov3333.timer.backend.model.dto.PeriodDto;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DailyPomodoroDto {

    private List<PomodoroDto> pomodoro;
    private boolean dayOff;
    private DayOfWeek dayOfWeek; //TODO: use getDayOfWeek() method instead
    private LocalDate date;

    public PeriodDto calculatePeriod() {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return new PeriodDto(start, end);
    }

}
