package com.igorgorbunov3333.timer.console.rest.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@AllArgsConstructor
public class PomodoroTagSaveRequestDto {

    @NotNull
    private final Set<String> tags;

}