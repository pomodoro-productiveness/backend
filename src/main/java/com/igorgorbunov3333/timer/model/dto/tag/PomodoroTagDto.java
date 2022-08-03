package com.igorgorbunov3333.timer.model.dto.tag;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class PomodoroTagDto {

    private final String name;
    private final boolean removed;

}
