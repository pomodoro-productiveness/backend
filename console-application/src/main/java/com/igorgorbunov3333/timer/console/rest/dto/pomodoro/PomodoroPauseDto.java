package com.igorgorbunov3333.timer.console.rest.dto.pomodoro;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PomodoroPauseDto {

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime startTime;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime endTime;

    @Override
    public String toString() {
        return "Pomodoro {" +
                ", startTime=" + startTime.toLocalDateTime() +
                ", endTime=" + endTime.toLocalDateTime() +
                '}';
    }

}