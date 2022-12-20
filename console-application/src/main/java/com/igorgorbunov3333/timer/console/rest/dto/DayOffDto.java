package com.igorgorbunov3333.timer.console.rest.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DayOffDto {

    @NotNull
    private LocalDate day;

}
