package com.igorgorbunov3333.timer.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igorgorbunov3333.timer.model.TemporalObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroDto implements TemporalObject {

    @JsonIgnore
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @Override
    public String toString() {
        return "Pomodoro {" +
                "id=" + id +
                ", startTime=" + startTime.truncatedTo(ChronoUnit.SECONDS) +
                ", endTime=" + endTime.truncatedTo(ChronoUnit.SECONDS) +
                '}';
    }

}
