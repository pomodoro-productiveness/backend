package com.igorgorbunov3333.timer.console.rest.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MessageDto {

    private LocalDate date;
    private String messagePeriod;

}