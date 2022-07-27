package com.igorgorbunov3333.timer.model.dto.pomodoro;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igorgorbunov3333.timer.model.TemporalObject;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"startTime", "endTime"})
public class PomodoroDto implements TemporalObject {

    @JsonIgnore
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime startTime;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime endTime;

    private boolean savedAutomatically;

    private List<PomodoroPauseDto> pomodoroPauses;

    private List<PomodoroTagDto> tags;

    @Override
    public String toString() {
        return "Pomodoro {" +
                "startTime=" + startTime.toLocalDateTime() +
                ", endTime=" + endTime.toLocalDateTime() +
                '}';
    }

}
