package com.igorgorbunov3333.timer.backend.model.dto.tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroTagDto {

    private String name;
    private boolean removed;

}
