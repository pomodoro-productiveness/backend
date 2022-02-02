package com.igorgorbunov3333.timer.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroDto {

    @JsonIgnore
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]")
    private LocalDateTime startTime;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]")
    private LocalDateTime endTime;

}
