package com.igorgorbunov3333.timer.console.rest.dto.pomodoro;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PomodoroSaveRequestDto {

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private final ZonedDateTime start;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private final ZonedDateTime end;

    private final List<PomodoroPauseDto> pauses;
    private final long tagGroupId;

}