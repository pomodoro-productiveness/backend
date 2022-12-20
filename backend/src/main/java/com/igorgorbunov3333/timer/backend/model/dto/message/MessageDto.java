package com.igorgorbunov3333.timer.backend.model.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MessageDto {

    @NotNull private LocalDate date;
    @NotBlank private String messagePeriod;

}
