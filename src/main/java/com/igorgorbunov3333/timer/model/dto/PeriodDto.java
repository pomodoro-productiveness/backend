package com.igorgorbunov3333.timer.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class PeriodDto {

    private LocalDateTime start;
    private LocalDateTime end;

}
