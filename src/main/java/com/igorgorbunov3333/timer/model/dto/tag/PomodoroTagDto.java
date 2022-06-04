package com.igorgorbunov3333.timer.model.dto.tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroTagDto {

    private String name;
    private List<PomodoroTagDto> children;
    private boolean removed;

}
